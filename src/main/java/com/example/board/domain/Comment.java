package com.example.board.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {

    @NotEmpty
    String content;
    LocalDateTime date;
}
