package com.petry.learning;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PasswordEncoderTest {

    @Test
    public void 비밀번호_암호화() throws Exception {

        String uPassword = "qwer1234";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(uPassword);

        assertThat(encodedPassword).startsWith("{");
        assertThat(encodedPassword).contains("{bcrypt}");
        assertThat(encodedPassword).isNotEqualTo(uPassword);

    }

    @Test
    public void 비밀번호_랜덤_암호화() throws Exception {

        String uPassword = "qwer1234";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(uPassword);
        String encodedPassword2 = passwordEncoder.encode(uPassword);

        assertThat(encodedPassword).isNotEqualTo(encodedPassword2);
    }

    @Test
    public void 비밀번호_매치() throws Exception {

        String uPassword = "qwer1234";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(uPassword);

        assertThat(passwordEncoder.matches(uPassword, encodedPassword)).isTrue();
    }
}
