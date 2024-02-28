package com.example.board.domain.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDto {

    @NotNull
    @NotBlank
    private String id;

    @NotNull
    @NotBlank
    private String pass;
}
