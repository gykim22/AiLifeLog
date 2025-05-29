package com.pnu.ailifelog.repository;

import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByTagNameAndUser(String tagName, User user);
    
    // 사용자별 위치 조회 (태그명 순)
    List<Location> findByUserOrderByTagNameAsc(User user);
}
