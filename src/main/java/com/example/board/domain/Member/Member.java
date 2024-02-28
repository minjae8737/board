package com.example.board.domain.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {


    @NotNull
    @NotBlank
    private String id; //아이디

    @NotNull
    @NotBlank
    private String pass; //비밀번호

    @NotEmpty
    @NotBlank
    private String nickName; //닉네임

}
