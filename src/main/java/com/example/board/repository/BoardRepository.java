package com.example.board.repository;

import com.example.board.domain.SearchType;
import com.example.board.domain.board.Post;
import com.example.board.domain.board.PostSearchDto;
import com.example.board.domain.board.UpdatePostDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    public List<String> findBoardList();

    public Post save(Post post);

    public void update(Long postId, UpdatePostDto updateParam);

    public Optional<Post> findById(Long postId);

    public List<Post> findAll(String boardName);

    public List<Post> findBySearchWord(String boardName,PostSearchDto postSearchDto);

    public void deleteById(Long postId);

    public void addHits(Long postId, int hits);
}
