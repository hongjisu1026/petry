package com.petry.domain.dto;

import java.util.Optional;

public record DiaryUpdateDto(Optional<String> title,
                             Optional<String> content) {

}
