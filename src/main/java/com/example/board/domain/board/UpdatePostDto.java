package com.example.board.domain.board;

import lombok.Data;

@Data
public class UpdatePostDto {
    private String title; //글제목
    private String content; //글내용
}
