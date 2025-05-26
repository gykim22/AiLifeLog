package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByLoginId(String loginId);
    Optional<User> findByLoginId(String loginId);
}
