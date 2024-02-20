package com.example.board.domain.board;

import com.example.board.domain.SearchType;
import lombok.Data;

@Data
public class PostSearchDto {
    private String searchWord; //검색어
    private SearchType searchType; //검색타입
}
