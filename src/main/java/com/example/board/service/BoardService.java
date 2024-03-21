package com.example.board.service;

import com.example.board.domain.board.*;
import com.example.board.domain.member.Member;
import com.example.board.repository.board.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<Board> findBoardList() {
        return boardRepository.findBoardList();
    }

    public Post savePost(String boardId, Post post, Member loginedMember) {
        post.setHits(0);
        post.setDate(LocalDateTime.now());
        post.setMemberNickname(loginedMember.getNickname());
        post.setBoardCategory(boardId);
        return boardRepository.savePost(post);
    }

    public void updatePost(Long postId, UpdatePostDto updateParam) {
        boardRepository.updatePost(postId, updateParam);
    }

    public Optional<Post> findById(Long postId) {
        return boardRepository.findPostById(postId);
    }

    public List<Post> findPosts(String boardId, PostSearchDto postSearchDto) {

        List<Post> posts;
        if (postSearchDto.getSearchWord() == null) {
            posts = boardRepository.findAllPosts(boardId);
        } else {
            posts = boardRepository.findBySearchWord(boardId, postSearchDto);
        }
        Collections.reverse(posts);
        return posts;
    }

    public void deleteById(String boardName, Long postId) {
        boardRepository.deletePostById(postId);
    }


    public void addHits(String boardName, Long postId, int hits) {
        boardRepository.addHits(postId, hits);
    }

    public void saveComment(String boardName, Long postId, Member loginedMember, Comment comment) {
        comment.setMemberNickname(loginedMember.getNickname());
        comment.setPostId(postId);
        comment.setDate(LocalDateTime.now());
        boardRepository.saveComment(postId, comment);
    }

    public void updateComment(String boardName, Long commentId, Comment comment) {

    }

    public void deleteCommentById(String boardName, Long commentId) {
        boardRepository.deleteCommentById(commentId);
    }

    public List<Comment> findAllCommentsByPostId(String boardName, Long postId) {
        List<Comment> comments = boardRepository.findAllCommentsByPostId(postId);
        return comments;
    }

}
