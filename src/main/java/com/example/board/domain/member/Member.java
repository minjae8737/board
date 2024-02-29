package com.example.board.domain.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * CREATE TABLE member (
 *     id VARCHAR(20) NOT NULL,
 *     pass VARCHAR(20) NOT NULL,
 *     nickName VARCHAR(20) NOT NULL,
 *     PRIMARY KEY (id)
 * );
 */

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
