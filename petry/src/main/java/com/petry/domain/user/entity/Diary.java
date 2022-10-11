package com.petry.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
;

@Table(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id; //pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(nullable = false)
    private String dContent;

    @Column(nullable = false)
    private String dTitle;

    @Column
    private String filePath;


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

    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }
}
