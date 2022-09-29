package com.petry.domain.pet;

import lombok.*;

import javax.persistence.*;

@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class PetImg {

    @Id @GeneratedValue
    @Column(name = "pimgId")
    private long id;

    @Column(nullable = false)
    private String pimgUrl;

    @Column(nullable = false)
    private String pimgName;

    @Column(nullable = false)
    private String poriImgName;

    @OneToOne(mappedBy = "petImg", fetch = FetchType.LAZY)
    private Pet pet;
}
