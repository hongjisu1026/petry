package com.petry.domain.user.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private long id; //PK

    @Column(nullable = false, unique = true, name = "u_account")
    private String uAccount; //ID

    @Column(name = "u_password")
    @Length(min = 8)
    private String uPassword; //PWD

    @Column(nullable = false, name = "u_name")
    private String uName; //이름(실명)

    @Column(nullable = false, unique = true, name = "u_email")
    private String uEmail; //이메일

    @Column(length = 1000)
    private String refreshToken; //Refresh Token


    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    //비밀번호 변경
    public void updatePassword(PasswordEncoder passwordEncoder, String uPassword) {
        this.uPassword = passwordEncoder.encode(uPassword);
    }

    //비밀번호 암호화
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.uPassword = passwordEncoder.encode(uPassword);
    }


    //회원 탈퇴시 모든 게시물 삭제
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaryList = new ArrayList<>();

    //연관관계 메서드
    public void addDiary(Diary diary) {
        diaryList.add(diary);
    }

    //비밀번호 변경, 회원 탈퇴 시 비밀번호 확인
    public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {
        return passwordEncoder.matches(checkPassword, getUPassword());
    }



}
