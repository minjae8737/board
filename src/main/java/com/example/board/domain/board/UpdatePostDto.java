package com.example.board.domain.board;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdatePostDto {
    @NotEmpty
    private String title; //글제목
    @NotEmpty
    private String content; //글내용
}
