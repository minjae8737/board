package com.example.board.repository;

import com.example.board.domain.SearchType;
import com.example.board.domain.board.Post;
import com.example.board.domain.board.PostSearchDto;
import com.example.board.domain.board.UpdatePostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class JDBCBoardRepository implements BoardRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate template;
    private final SimpleJdbcInsert jdbcInsert;

    public JDBCBoardRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("board")
                .usingGeneratedKeyColumns("id");
    }

    public List<String> findBoardList() {
        String sql = "show tables";

        List<Map<String,Object>> resultSet;

        try {
            resultSet = jdbcTemplate.queryForList(sql);
        } catch (EmptyResultDataAccessException e) {
            resultSet = new ArrayList<>();
        }

        List<String> boardNameList = new ArrayList<>();

        for (Map<String, Object> result : resultSet) {
            boardNameList.add((String) result.values().toArray()[0]);
        }

        return boardNameList;
    }

    public Post save(Post post) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(post);
        Number key = jdbcInsert.executeAndReturnKey(param);
        post.setId(key.longValue());
        return post;
    }

    public void update(Long postId, UpdatePostDto updateParam) {
        String sql = "update board " +
                "set title=:title, content=:content " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("title", updateParam.getTitle())
                .addValue("content", updateParam.getContent())
                .addValue("id", postId);
        template.update(sql, param);
    }

    public Optional<Post> findById(Long postId) {
        String sql = "select id, title, content, date, hits " +
                "from board " +
                "where id = :id";

        try {
            SqlParameterSource param = new MapSqlParameterSource()
                    .addValue("id", postId);
            Post post = template.queryForObject(sql, param, postRowMapper());
            return Optional.of(post);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Post> findAll(String boardName) {
        String sql = "select id, title, content, date, hits " +
                "from :boardName";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("boardName", "board_" + boardName);

        return template.query(sql, postRowMapper());
    }

    public List<Post> findBySearchWord(String boardName, PostSearchDto postSearchDto) {
        String sql = "select id, title, content, date, hits " +
                "from :boardName " +
                "where ";

        SearchType searchType = postSearchDto.getSearchType();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("boardName", "board_" + boardName)
                .addValue("searchWord", postSearchDto.getSearchWord())
                .addValue("searchType", postSearchDto.getSearchType());

//        SqlParameterSource param = new BeanPropertySqlParameterSource(postSearchDto);

        if (searchType == SearchType.TITLE) {
            sql += "title like concat('%',:searchWord,'%')";
        } else if (searchType == SearchType.CONTENT) {
            sql += "content like concat('%',:searchWord,'%')";
        } else {
            sql += "title like concat('%',:searchWord,'%') or content like concat('%',:searchWord,'%')";
        }

        log.info("sql ={}", sql);
        return template.query(sql, param, postRowMapper());
    }

    public void deleteById(Long boardId) {
        String sql = "delete from board " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", boardId);
        template.update(sql, param);
    }

    public void addHits(Long boardId, int hits) {
        String sql = "update board " +
                "set hits=:hits " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("hits", hits)
                .addValue("id", boardId);

        template.update(sql, param);
    }

    private RowMapper<Post> postRowMapper() {
        return BeanPropertyRowMapper.newInstance(Post.class);
    }
}
