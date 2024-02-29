package com.example.board.repository.board;

import com.example.board.domain.board.Post;
import com.example.board.domain.board.PostSearchDto;
import com.example.board.domain.board.UpdatePostDto;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    public List<String> findBoardList();

    public Post savePost(String boardName, Post post);

    public void updatePost(String boardName,Long postId, UpdatePostDto updateParam);

    public Optional<Post> findById(String boardName,Long postId);

    public List<Post> findAllPosts(String boardName);

    public List<Post> findBySearchWord(String boardName,PostSearchDto postSearchDto);

    public void deleteById(String boardName,Long postId);

    public void addHits(String boardName,Long postId, int hits);
}
