package com.petry.domain.user;

import com.petry.domain.BaseTimeEntity;
import com.petry.domain.pet.Pet;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.Role;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uId")
    private long id; //PK

    @Column(nullable = false, unique = true)
    private String uAccount; //ID

    @Length(min = 8)
    private String uPassword; //PWD

    @Column(nullable = false)
    private String uName; //이름(실명)

    @Column(nullable = false, unique = true)
    private String uEmail; //이메일

    @OneToMany(mappedBy = "user")
    private List<Pet> pet = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role; //권한

    //비밀번호 변경
    public void updatePassword(PasswordEncoder passwordEncoder, String uPassword) {
        this.uPassword = passwordEncoder.encode(uPassword);
    }

    //비밀번호 암호화
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.uPassword = passwordEncoder.encode(uPassword);
    }

}
