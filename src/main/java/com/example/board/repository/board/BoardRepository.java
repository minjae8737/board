package com.example.board.repository.board;

import com.example.board.domain.board.*;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    public List<Board> findBoardList();

    public Post savePost(Post post);

    public void updatePost(Long postId, UpdatePostDto updateParam);

    public Optional<Post> findPostById(Long postId);

    public List<Post> findAllPosts(String boardId);

    public List<Post> findBySearchWord(String boardId, PostSearchDto postSearchDto);

    public void deletePostById(Long postId);

    public void addHits(Long postId, int hits);

    public Comment saveComment(Long postId, Comment comment);

    public void updateComment(String boardId, Long commentId);

    public void deleteCommentById(Long commentId);

    public List<Comment> findAllCommentsByPostId(Long postId);

}
