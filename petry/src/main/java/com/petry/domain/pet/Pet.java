package com.petry.domain.pet;


import com.petry.domain.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Pet {

    @Id @GeneratedValue
    @Column(name = "pId")
    private Long id; //PK

    @Column(nullable = false)
    private String pName;

    @Column(nullable = false)
    private LocalDateTime pBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pimgId")
    private PetImg petImg;

    @ManyToOne
    @JoinColumn(name = "user_u_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }
}
