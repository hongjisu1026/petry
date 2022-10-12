package com.petry.domain.entity;

import lombok.*;

import javax.persistence.*;
;import java.util.ArrayList;
import java.util.List;

@Table(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Diary extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "d_id")
    private Long id; //pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_id")
    private User user;

    @Lob
    @Column(nullable = false, name = "d_content")
    private String dContent;

    @Column(nullable = false, name = "d_title")
    private String dTitle;


    //연관관계 메서드
    public void confirmUser(User user) {
        this.user = user;
        user.addDiary(this);
    }

    //내용 수정
    public void updateTitle(String title) {
        this.dTitle = title;
    }

    public void updateContent(String content) {
        this.dContent = content;
    }

}
