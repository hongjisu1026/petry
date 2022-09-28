package entity;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.Role;
import javax.persistence.*;

@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uId")
    private long id; //PK

    @Column(nullable = false, unique = true)
    private String uAccount; //ID

    private String uPassword; //PWD

    @Column(nullable = false)
    private String uName; //이름(실명)

    @Column(nullable = false)
    private String uEmail; //이메일

    @Enumerated(EnumType.STRING)
    private Role role; //권한

    //비밀번호 변경
    public void updateuPassword(PasswordEncoder passwordEncoder, String uPassword){
        this.uPassword = passwordEncoder.encode(uPassword);
    }

    //비밀번호 암호화
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.uPassword = passwordEncoder.encode(uPassword);
    }
}
