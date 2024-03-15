package com.example.board.repository.board;

import com.example.board.domain.board.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
public class JdbcBoardRepository implements BoardRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate template;
//    private final SimpleJdbcInsert jdbcInsert;

    public JdbcBoardRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.template = new NamedParameterJdbcTemplate(dataSource);
//        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
//                .withTableName("board")
//                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<String> findBoardList() {
        String sql = "show tables";

        List<Map<String, Object>> resultSet;

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

    @Override
    public Post savePost(String boardName, Post post) {
        String tableName = "board_" + boardName;

        String sql = "insert into " + tableName + " (title, content, date, hits,nickname) " +
                "values (:title, :content, :date, :hits, :nickname)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(post);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        post.setId(key);
        return post;
    }

    @Override
    public void updatePost(String boardName, Long postId, UpdatePostDto updateParam) {
        String tableName = "board_" + boardName;

        String sql = "update " + tableName + " " +
                "set title=:title, content=:content " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("title", updateParam.getTitle())
                .addValue("content", updateParam.getContent())
                .addValue("id", postId);
        template.update(sql, param);
    }

    @Override
    public Optional<Post> findPostById(String boardName, Long postId) {
        String tableName = "board_" + boardName;

        String sql = "select id, title, content, date, hits, nickname " +
                "from " + tableName + " " +
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

    @Override
    public List<Post> findAllPosts(String boardName) {
        String tableName = "board_" + boardName;

        String sql = "select id, title, content, date, hits, nickname " +
                "from " + tableName;

        return template.query(sql, postRowMapper());
    }

    @Override
    public List<Post> findBySearchWord(String boardName, PostSearchDto postSearchDto) {

        String tableName = "board_" + boardName;

        String sql = "select id, title, content, date, hits, nickname " +
                "from " + tableName + " " +
                "where ";

        SearchType searchType = postSearchDto.getSearchType();
        SqlParameterSource param = new MapSqlParameterSource()
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

    @Override
    public void deletePostById(String boardName, Long postId) {
        String tableName = "board_" + boardName;

        String sql = "delete from " + tableName + " " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", postId);
        template.update(sql, param);
    }

    @Override
    public void addHits(String boardName, Long postId, int hits) {
        String tableName = "board_" + boardName;

        String sql = "update " + tableName + " " +
                "set hits=:hits " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("hits", hits)
                .addValue("id", postId);

        template.update(sql, param);
    }

    @Override
    public Comment saveComment(String boardName, Long postId, Comment comment) {
        String tableName = "comment_" + boardName;

        String sql = "insert into " + tableName + " (comment_content, member_nickname, post_id, date) " +
                "values (:commentContent, :memberNickname, :postId, :date)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(comment);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        comment.setId(key);

        return comment;
    }

    @Override
    public void updateComment(String boardName, Long commentId) {
        String tableName = "comment_" + boardName;

    }

    @Override
    public void deleteCommentById(String boardName, Long commentId) {
        String tableName = "comment_" + boardName;

        String sql = "delete from " + tableName + " " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", commentId);

        template.update(sql, param);
    }

    @Override
    public List<Comment> findAllCommentsByPostId(String boardName, Long postId) {
        String tableName = "comment_" + boardName;

        String sql = "select id, comment_content, member_nickname, post_id, date " +
                "from " + tableName + " " +
                "where post_id=:postId";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId);

        return template.query(sql, param, commentRowMapper());
    }

    private RowMapper<Post> postRowMapper() {
        return BeanPropertyRowMapper.newInstance(Post.class);
    }

    private RowMapper<Comment> commentRowMapper() {
        return BeanPropertyRowMapper.newInstance(Comment.class);
    }
}
