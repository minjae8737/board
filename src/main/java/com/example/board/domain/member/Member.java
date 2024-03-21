package com.example.board.domain.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
CREATE TABLE member (
    id VARCHAR(20) NOT NULL,
    pass VARCHAR(20) NOT NULL,
    nickname VARCHAR(20) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);
*/

@Data
@Entity
public class Member {


    @NotNull
    @NotBlank
    @Id
    private String id; //아이디

    @NotNull
    @NotBlank
    private String pass; //비밀번호

    @NotEmpty
    @NotBlank
    private String nickname; //닉네임

    public Member() {
    }
}
