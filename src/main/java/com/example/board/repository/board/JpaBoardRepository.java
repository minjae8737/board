package com.example.board.repository.board;

import com.example.board.domain.board.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class JpaBoardRepository implements BoardRepository {

    private final EntityManager em;

    @Override
    public List<String> findBoardList() {

        List<Object[]> resultList = em.createNativeQuery("show tables").getResultList();

        List<String> boardNameList = new ArrayList<>();

        for (Object[] result : resultList) {
            boardNameList.add(result[0].toString());
        }

        return boardNameList;
    }

    @Override
    public Post savePost(String boardName, Post post) {
        String tableName = "board_" + boardName;

        String sql = "insert into " + tableName + " (title, content, date, hits,nickname) " +
                "values (:title, :content, :date, :hits, :nickname)";

        em.createNativeQuery(sql)
                .setParameter("title", post.getTitle())
                .setParameter("content", post.getContent())
                .setParameter("date", post.getDate())
                .setParameter("hits", post.getHits())
                .setParameter("nickname", post.getNickname()).executeUpdate();

        //키값 어떻게 얻지
        log.info("post.getId()={}", post.getId());


        return null;
    }

    @Override
    public void updatePost(String boardName, Long postId, UpdatePostDto updateParam) {
        String tableName = "board_" + boardName;

        String sql = "update " + tableName + " " +
                "set title=:title, content=:content " +
                "where id=:id";

        em.createNativeQuery(sql)
                .setParameter("title", updateParam.getTitle())
                .setParameter("content", updateParam.getContent())
                .executeUpdate();
    }

    @Override
    public Optional<Post> findPostById(String boardName, Long postId) {
        String tableName = "board_" + boardName;

        String sql = "select id, title, content, date, hits, nickname " +
                "from " + tableName + " " +
                "where id = :id";

        try {
            Post findPost = (Post) em.createNativeQuery(sql, Post.class)
                    .setParameter("id", postId)
                    .getSingleResult();

            return Optional.of(findPost);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Post> findAllPosts(String boardName) {
        String tableName = "board_" + boardName;

        String sql = "select id, title, content, date, hits, nickname " +
                "from " + tableName;

        return em.createNativeQuery(sql, Post.class).getResultList();
    }

    @Override
    public List<Post> findBySearchWord(String boardName, PostSearchDto postSearchDto) {
        String tableName = "board_" + boardName;

        String sql = "select id, title, content, date, hits, nickname " +
                "from " + tableName + " " +
                "where ";

        SearchType searchType = postSearchDto.getSearchType();

        if (searchType == SearchType.TITLE) {
            sql += "title like concat('%',:searchWord,'%')";
        } else if (searchType == SearchType.CONTENT) {
            sql += "content like concat('%',:searchWord,'%')";
        } else {
            sql += "title like concat('%',:searchWord,'%') or content like concat('%',:searchWord,'%')";
        }

        log.info("sql ={}", sql);

        return em.createNativeQuery(sql, Post.class)
                .setParameter("searchWord", postSearchDto.getSearchWord())
                .setParameter("searchType", postSearchDto.getSearchType())
                .getResultList();
    }

    @Override
    public void deletePostById(String boardName, Long postId) {
        String tableName = "board_" + boardName;

        String sql = "delete from " + tableName + " " +
                "where id=:id";

        em.createNativeQuery(sql)
                .setParameter("id", postId)
                .executeUpdate();
    }

    @Override
    public void addHits(String boardName, Long postId, int hits) {
        String tableName = "board_" + boardName;

        String sql = "update " + tableName + " " +
                "set hits=:hits " +
                "where id=:id";

        em.createNativeQuery(sql)
                .setParameter("hits", hits)
                .setParameter("id", postId)
                .executeUpdate();
    }

    @Override
    public Comment saveComment(String boardName, Long postId, Comment comment) {
        String tableName = "comment_" + boardName;

        String sql = "insert into " + tableName + " (comment_content, member_nickname, post_id, date) " +
                "values (:commentContent, :memberNickname, :postId, :date)";

        em.createNativeQuery(sql)
                .setParameter("commentContent", comment.getCommentContent())
                .setParameter("memberNickname", comment.getMemberNickname())
                .setParameter("postId", comment.getPostId())
                .setParameter("date", comment.getDate())
                .executeUpdate();

        // 여기도 어떻게 할지 키값을

        return null;
    }

    @Override
    public void updateComment(String boardName, Long commentId) {

    }

    @Override
    public void deleteCommentById(String boardName, Long commentId) {
        String tableName = "comment_" + boardName;

        String sql = "delete from " + tableName + " " +
                "where id=:id";

        em.createNativeQuery(sql)
                .setParameter("id", commentId)
                .executeUpdate();
    }

    @Override
    public List<Comment> findAllCommentsByPostId(String boardName, Long postId) {
        String tableName = "comment_" + boardName;

        String sql = "select id, comment_content, member_nickname, post_id, date " +
                "from " + tableName + " " +
                "where post_id=:postId";

        return em.createNativeQuery(sql, Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}
