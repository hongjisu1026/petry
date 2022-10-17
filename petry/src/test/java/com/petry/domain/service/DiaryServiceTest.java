package com.petry.domain.service;

import com.petry.domain.dto.DiarySaveDto;
import com.petry.domain.dto.DiaryUpdateDto;
import com.petry.domain.dto.UserSignUpDto;
import com.petry.domain.entity.Diary;
import com.petry.domain.entity.User;
import com.petry.domain.exception.DiaryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class DiaryServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private DiaryService diaryService;
    @Autowired
    private UserService userService;

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "qwer1234!";

    private void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void signUpAndSetAuthentication() throws Exception {
        userService.signUp(new UserSignUpDto(ACCOUNT, PASSWORD, "name", "user@email.com"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .uAccount(ACCOUNT)
                                .uPassword(PASSWORD)
                                .build(), null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 다이어리_업로드_성공() throws Exception {
        String title= "제목입니당";
        String content = "내용입니당";
        DiarySaveDto diarySaveDto = new DiarySaveDto(title, content);

        diaryService.save(diarySaveDto);
        clear();

        Diary findDiary = em.createQuery("select d from Diary d", Diary.class).getSingleResult();
        Diary diary = em.find(Diary.class, findDiary.getId());
        assertThat(diary.getDContent()).isEqualTo(content);
        assertThat(diary.getUser().getUAccount()).isEqualTo(ACCOUNT);
    }

    @Test
    public void 다이어리_업로드_실패_재목or내용_없음() throws Exception {
        String title= "제목입니당";
        String content = "내용입니당";

        DiarySaveDto diarySaveDto = new DiarySaveDto(null, content);
        DiarySaveDto diarySaveDto1 = new DiarySaveDto(title, null);

        assertThrows(Exception.class, () -> diaryService.save(diarySaveDto));
        assertThrows(Exception.class, () -> diaryService.save(diarySaveDto1));
    }


    private void setAnotherAuthentication() throws Exception {
        userService.signUp(new UserSignUpDto(ACCOUNT+"123", PASSWORD, "user", "user@email.com"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .uAccount(ACCOUNT+"123")
                                .uPassword(PASSWORD)
                                .build(), null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    @Test
    public void 다이어리_업데이트_실패_권한X() throws Exception {
        String title= "제목입니당";
        String content = "내용입니당";
        DiarySaveDto diarySaveDto = new DiarySaveDto(title, content);

        diaryService.save(diarySaveDto);
        clear();

        setAnotherAuthentication();
        Diary findDiary = em.createQuery("select d from Diary d", Diary.class).getSingleResult();
        DiaryUpdateDto diaryUpdateDto = new DiaryUpdateDto(Optional.ofNullable("새로운 제목입니당"), Optional.ofNullable("새로운 내용입니당"));

        assertThrows(DiaryException.class, () -> diaryService.update(findDiary.getId(), diaryUpdateDto));
    }

    @Test
    public void 다이어리_삭제_성공() throws Exception {
        String title= "제목입니당";
        String content = "내용입니당";
        DiarySaveDto diarySaveDto = new DiarySaveDto(title, content);

        diaryService.save(diarySaveDto);
        clear();

        Diary findDiary = em.createQuery("select d from Diary d", Diary.class).getSingleResult();
        diaryService.delete(findDiary.getId());

        List<Diary> findDiaryList = em.createQuery("select d from Diary d", Diary.class).getResultList();
        assertThat(findDiaryList.size()).isEqualTo(0);
    }

    @Test
    public void 다이어리_삭제_실패() throws Exception {
        String title= "제목입니당";
        String content = "내용입니당";
        DiarySaveDto diarySaveDto = new DiarySaveDto(title, content);

        diaryService.save(diarySaveDto);
        clear();

        setAnotherAuthentication();
        Diary findDiary = em.createQuery("select d from Diary d", Diary.class).getSingleResult();
        assertThrows(DiaryException.class, () -> diaryService.delete(findDiary.getId()));
    }



}