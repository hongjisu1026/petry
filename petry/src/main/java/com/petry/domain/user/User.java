package com.petry.domain.user;

import com.petry.domain.BaseTimeEntity;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.Role;
import javax.persistence.*;

@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id; //PK

    @Column(nullable = false, unique = true)
    private String uAccount; //ID

    @Length(min = 8)
    private String uPassword; //PWD

    @Column(nullable = false)
    private String uName; //이름(실명)

    @Column(nullable = false, unique = true)
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




}
