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
import java.util.Optional;

@Slf4j
@Repository
@Transactional
public class JdbcBoardRepository implements BoardRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcBoardRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
//        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
//                .withTableName("board")
//                .usingGeneratedKeyColumns("id");
    }

    @Override
    public List<Board> findBoardList() {
        String sql = "select id, name " +
                "from board";

        List<Board> boardNameList;

        try {
            boardNameList = template.query(sql, boardRowMapper());
        } catch (EmptyResultDataAccessException e) {
            boardNameList = new ArrayList<>();
        }

        return boardNameList;
    }

    @Override
    public Post savePost(Post post) {
        String sql = "insert into post (title, post_content, date, hits, member_nickname, board_category) " +
                "values (:title, :postContent, :date, :hits, :memberNickname, :boardCategory)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(post);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        post.setId(key);
        return post;
    }

    @Override
    public void updatePost(Long postId, UpdatePostDto updateParam) {
        String sql = "update post " +
                "set title=:title, post_content=:postContent " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("title", updateParam.getTitle())
                .addValue("postContent", updateParam.getPostContent())
                .addValue("id", postId);
        template.update(sql, param);
    }

    @Override
    public Optional<Post> findPostById(Long postId) {
        String sql = "select id, title, post_content, date, hits, member_nickname, board_category " +
                "from post " +
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
    public List<Post> findAllPosts(String boardId) {
        String sql = "select id, title, post_content, date, hits, member_nickname, board_category " +
                "from post " +
                "where board_category=:boardId";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("boardId", boardId);

        return template.query(sql, param, postRowMapper());
    }

    @Override
    public List<Post> findBySearchWord(String boardId, PostSearchDto postSearchDto) {

        String sql = "select id, title, post_content, date, hits, member_nickname, board_category " +
                "from post " +
                "where board_category=:boardId ";

        SearchType searchType = postSearchDto.getSearchType();
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("boardId", boardId)
                .addValue("searchWord", postSearchDto.getSearchWord());

        if (searchType == SearchType.TITLE) {
            sql += "and title like concat('%',:searchWord,'%')";
        } else if (searchType == SearchType.CONTENT) {
            sql += "and post_content like concat('%',:searchWord,'%')";
        } else {
            sql += "and (title like concat('%',:searchWord,'%') or post_content like concat('%',:searchWord,'%'))";
        }

        log.info("sql ={}", sql);
        return template.query(sql, param, postRowMapper());
    }

    @Override
    public void deletePostById(Long postId) {
        String sql = "delete from post " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", postId);
        template.update(sql, param);
    }

    @Override
    public void addHits(Long postId, int hits) {
        String sql = "update post " +
                "set hits=:hits " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("hits", hits)
                .addValue("id", postId);

        template.update(sql, param);
    }

    @Override
    public Comment saveComment(Long postId, Comment comment) {
        String sql = "insert into comment (comment_content, member_nickname, post_id, date) " +
                "values (:commentContent, :memberNickname, :postId, :date)";

        SqlParameterSource param = new BeanPropertySqlParameterSource(comment);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        comment.setId(key);

        return comment;
    }

    @Override
    public void updateComment(UpdateCommentDto updateParam) {
        String sql = "update comment " +
                "set comment_content=:commentContent " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("commentContent", updateParam.getEditedContent())
                .addValue("id", updateParam.getCommentId());

        template.update(sql, param);
    }

    @Override
    public void deleteCommentById(Long commentId) {
        String sql = "delete from comment " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", commentId);

        template.update(sql, param);
    }

    @Override
    public List<Comment> findAllCommentsByPostId(Long postId) {
        String sql = "select id, comment_content, member_nickname, post_id, date " +
                "from comment " +
                "where post_id=:postId";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("postId", postId);

        return template.query(sql, param, commentRowMapper());
    }

    private RowMapper<Board> boardRowMapper() {
        return BeanPropertyRowMapper.newInstance(Board.class);
    }

    private RowMapper<Post> postRowMapper() {
        return BeanPropertyRowMapper.newInstance(Post.class);
    }

    private RowMapper<Comment> commentRowMapper() {
        return BeanPropertyRowMapper.newInstance(Comment.class);
    }
}
