package com.petry.domain.user.repository;

import com.petry.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String uAccount);

    boolean existsByUsername(String uAccount);
}
