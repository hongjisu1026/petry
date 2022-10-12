package com.petry.domain.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "pets")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
public class Pet extends BaseTimeEntity {

    @Id
    @Column(name = "p_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; //PK

    @Column(name = "p_name")
    private String pName; //이름

    @Column(name = "p_birth", length = 8)
    private String pBirth; //생년월일

    @Column(name = "p_sex",length = 1, columnDefinition = "CHAR(1) DEFAILT '0' ")
    private String pSex; //성별

    @Column(name = "p_img")
    private String pImg; //프로필 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "d_id")
    private Diary diary;

}
