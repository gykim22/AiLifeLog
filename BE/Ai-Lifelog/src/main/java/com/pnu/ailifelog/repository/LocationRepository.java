package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByTagNameAndUser(String tagName, User user);
}
