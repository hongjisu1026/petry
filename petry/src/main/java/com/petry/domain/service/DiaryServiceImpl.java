package com.petry.domain.service;

import com.petry.domain.dto.DiarySaveDto;
import com.petry.domain.dto.DiaryUpdateDto;
import com.petry.domain.entity.Diary;
import com.petry.domain.exception.DiaryException;
import com.petry.domain.exception.DiaryExceptionType;
import com.petry.domain.exception.UserException;
import com.petry.domain.exception.UserExceptionType;
import com.petry.domain.repository.DiaryRepository;
import com.petry.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryServiceImpl implements DiaryService{

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Override
    public void save(DiarySaveDto diarySaveDto) throws DiaryException {
        Diary diary = diarySaveDto.toEntity();

        diary.confirmUser(userRepository.findByuAccount(SecurityUtil.getLoginAccount())
                .orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER)));

        diaryRepository.save(diary);
    }

    @Override
    public void update(Long id, DiaryUpdateDto diaryUpdateDto) {
        Diary diary = diaryRepository.findById(id).orElseThrow(() -> new DiaryException(DiaryExceptionType.DIARY_NOT_FOUNT));

        checkAuthority(diary, DiaryExceptionType.NOT_AUTHORITY_UPDATE);

        diaryUpdateDto.title().ifPresent(diary::updateTitle);
        diaryUpdateDto.content().ifPresent(diary::updateContent);
    }

    @Override
    public void delete(Long id) {
        Diary diary = diaryRepository.findById(id).orElseThrow(() -> new DiaryException(DiaryExceptionType.DIARY_NOT_FOUNT));

        checkAuthority(diary, DiaryExceptionType.NOT_AUTHORITY_DELETE);

        diaryRepository.delete(diary);
    }

    private void checkAuthority(Diary diary, DiaryExceptionType diaryExceptionType){
        if(!diary.getUser().getUAccount().equals(SecurityUtil.getLoginAccount())) throw new DiaryException(diaryExceptionType);
    }


}
