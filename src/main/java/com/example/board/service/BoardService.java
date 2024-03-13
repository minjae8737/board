package com.example.board.service;

import com.example.board.domain.board.Comment;
import com.example.board.domain.board.Post;
import com.example.board.domain.board.PostSearchDto;
import com.example.board.domain.board.UpdatePostDto;
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

    public List<String> findBoardList() {
        List<String> boardNameList = boardRepository.findBoardList();

        String compareStr = "board_";

        List<String> newboardNameList =
                boardNameList.stream()
                        .filter(s -> s.toLowerCase().contains(compareStr))
                        .map(s -> s.substring(compareStr.length()))
                        .collect(Collectors.toList());

        return newboardNameList;
    }

    public Post savePost(String boardName, Post post, Member loginedMember) {
        post.setHits(0);
        post.setDate(LocalDateTime.now());
        post.setNickname(loginedMember.getNickname());
        return boardRepository.savePost(boardName, post);
    }

    public void updatePost(String boardName, Long postId, UpdatePostDto updateParam) {
        boardRepository.updatePost(boardName, postId, updateParam);
    }

    public Optional<Post> findById(String boardName, Long postId) {
        return boardRepository.findPostById(boardName, postId);
    }

    public List<Post> findPosts(String boardName, PostSearchDto postSearchDto) {

        List<Post> posts;
        if (postSearchDto.getSearchWord() == null) {
            posts = boardRepository.findAllPosts(boardName);
        } else {
            posts = boardRepository.findBySearchWord(boardName, postSearchDto);
        }
        Collections.reverse(posts);
        return posts;
    }

    public void deleteById(String boardName, Long postId) {
        boardRepository.deletePostById(boardName, postId);
    }


    public void addHits(String boardName, Long postId, int hits) {
        boardRepository.addHits(boardName, postId, hits);
    }

    public void saveComment(String boardName, Long postId, Member loginedMember, Comment comment) {
        comment.setMemberNickname(loginedMember.getNickname());
        comment.setPostId(postId);
        comment.setDate(LocalDateTime.now());
        boardRepository.saveComment(boardName, postId, comment);
    }

    public void updateComment(String boardName, Long commentId, Comment comment) {

    }

    public void deleteCommentById(String boardName, Long commentId) {
        boardRepository.deleteCommentById(boardName, commentId);
    }

    public List<Comment> findAllCommentsByPostId(String boardName, Long postId) {
        List<Comment> comments = boardRepository.findAllCommentsByPostId(boardName, postId);
        return comments;
    }

}
