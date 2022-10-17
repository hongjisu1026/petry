package com.petry.domain.service;

import com.petry.domain.dto.DiarySaveDto;
import com.petry.domain.dto.DiaryUpdateDto;
import com.petry.domain.entity.Diary;

public interface DiaryService {

    void save(DiarySaveDto diarySaveDto) throws Exception;

    void update(Long id, DiaryUpdateDto diaryUpdateDto) throws Exception;

    void delete(Long id) throws Exception;

}
