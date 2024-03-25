package com.example.board.domain.board;

import lombok.Data;

@Data
public class UpdateCommentDto {

    private long commentId;
    private String editedContent;
}
