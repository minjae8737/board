package com.example.board.service;

import com.example.board.domain.board.Post;
import com.example.board.domain.board.PostSearchDto;
import com.example.board.domain.board.UpdatePostDto;
import com.example.board.repository.BoardRepository;
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

        List<String> newboardNameList = boardNameList.stream()
                .map(s -> s.substring("board_".length()))
                .collect(Collectors.toList());

        return newboardNameList;
    }

    public Post savePost(String boardName, Post post) {
        post.setHits(0);
        post.setDate(LocalDateTime.now());
        return boardRepository.savePost(boardName, post);
    }

    public void updatePost(String boardName, Long postId, UpdatePostDto updateParam) {
        boardRepository.updatePost(boardName, postId, updateParam);
    }

    public Optional<Post> findById(String boardName, Long postId) {
        return boardRepository.findById(boardName, postId);
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
        boardRepository.deleteById(boardName, postId);
    }


    public void addHits(String boardName, Long postId, int hits) {
        boardRepository.addHits(boardName, postId, hits);
    }


}
