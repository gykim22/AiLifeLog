package com.pnu.ailifelogv2.repository;


import com.pnu.ailifelogv2.entity.User;
import io.micrometer.context.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
