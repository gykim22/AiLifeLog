package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "위치 태그명을 입력하세요.")
    @Size(min = 1, max = 50, message = "위치 태그명은 1자 이상 50자 이하로 입력하세요.")
    private String tagName;

    // GPS 좌표는 선택적 - 사용자가 태그만 생성하고 나중에 위치 정보 추가 가능
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public String toJSONString() {
        if (latitude != null && longitude != null) {
            return String.format("{\"id\": %d, \"tagName\": \"%s\", \"latitude\": %.6f, \"longitude\": %.6f}",
                    id, tagName, latitude, longitude);
        } else {
            return String.format("{\"id\": %d, \"tagName\": \"%s\"}",
                    id, tagName);
        }
    }
}
