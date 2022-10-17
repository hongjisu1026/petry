package com.petry.domain.dto;

import com.petry.domain.entity.Diary;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public record DiarySaveDto(@NotBlank(message = "제목을 입력해주세요.") String title,
                           @NotBlank(message = "내용을 입력해주세요") String content) {

    public Diary toEntity() {
        return Diary.builder().dTitle(title).dContent(content).build();
    }
}
