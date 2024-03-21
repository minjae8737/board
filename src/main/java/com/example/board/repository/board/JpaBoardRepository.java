package com.example.board.repository.board;

import com.example.board.domain.board.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional
@RequiredArgsConstructor
public class JpaBoardRepository implements BoardRepository {

    private final EntityManager em;

    @Override
    public List<Board> findBoardList() {
        String jpql = "select b from Board b";
        List<Board> boards = em.createQuery(jpql, Board.class).getResultList();
        return boards;
    }

    @Override
    public Post savePost(Post post) {
        em.persist(post);
        return post;
    }

    @Override
    public void updatePost(Long postId, UpdatePostDto updateParam) {
        Post findPost = em.find(Post.class, postId);
        findPost.setTitle(updateParam.getTitle());
        findPost.setPostContent(updateParam.getPostContent());
    }

    @Override
    public Optional<Post> findPostById(Long postId) {
        Post findPost = em.find(Post.class, postId);
        return Optional.ofNullable(findPost);
    }

    @Override
    public List<Post> findAllPosts(String boardId) {
        String jpql = "select p " +
                "from Post p " +
                "where p.boardCategory=:boardId";
        Query query = em.createQuery(jpql);
        query.setParameter("boardId", boardId);
        return query.getResultList();
    }

    @Override
    public List<Post> findBySearchWord(String boardId, PostSearchDto postSearchDto) {
        String jpql = "select p " +
                "from Post p " +
                "where p.boardCategory=:boardId ";

        SearchType searchType = postSearchDto.getSearchType();
        String searchWord = postSearchDto.getSearchWord();

        if (searchType == SearchType.TITLE) {
            jpql += "and p.title like concat('%',:searchWord,'%')";
        } else if (searchType == SearchType.CONTENT) {
            jpql += "and p.postContent like concat('%',:searchWord,'%')";
        } else {
            jpql += "and (p.title like concat('%',:searchWord,'%') or p.postContent like concat('%',:searchWord,'%'))";
        }

        Query query = em.createQuery(jpql);
        query.setParameter("boardId", boardId);
        query.setParameter("searchWord", searchWord);

        log.info("jpql ={}", jpql);

        return query.getResultList();
    }

    @Override
    public void deletePostById(Long postId) {
        Post post = em.getReference(Post.class, postId);
        em.remove(post);
    }

    @Override
    public void addHits(Long postId, int hits) {
        Post findPost = em.find(Post.class, postId);
        findPost.setHits(hits);
    }

    @Override
    public Comment saveComment(Long postId, Comment comment) {
        em.persist(comment);
        return comment;
    }

    @Override
    public void updateComment(String boardId, Long commentId) {

    }

    @Override
    public void deleteCommentById(Long commentId) {
        Comment findComment = em.getReference(Comment.class, commentId);
        em.remove(findComment);
    }

    @Override
    public List<Comment> findAllCommentsByPostId(Long postId) {
        String jpql = "select c " +
                "from Comment c " +
                "where c.postId=:postId";
        Query query = em.createQuery(jpql)
                .setParameter("postId", postId);

        return query.getResultList();
    }
}

