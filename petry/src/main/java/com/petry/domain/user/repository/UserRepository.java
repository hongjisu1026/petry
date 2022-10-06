package com.petry.domain.user.repository;

import com.petry.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByuAccount(String account);

    boolean existsByuAccount (String account);

    Optional<User> findByRefreshToken(String refreshToken);

}
