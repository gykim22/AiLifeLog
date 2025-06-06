package com.pnu.ailifelog.component.tool;

import com.pnu.ailifelog.entity.Location;
import com.pnu.ailifelog.entity.User;
import com.pnu.ailifelog.repository.LocationRepository;
import org.springframework.ai.tool.annotation.Tool;
import java.util.List;
import java.util.stream.Collectors;

public class UserLocationTools {
    private LocationRepository locationRepository;
    private User owner;

    public UserLocationTools(LocationRepository locationRepository, User owner) {
        this.locationRepository = locationRepository;
        this.owner = owner;
    }

    @Tool(
            name = "getAllUserLocations",
            description = """
    사용자의 모든 위치 정보를 가져옵니다. 이 도구는 사용자가 저장한 위치의 태그 이름을 가져오는 데 사용됩니다.
    AI는 이 도구를 활용하여  사용자가 이미 만들어 놓은 위치 태그를 확인하고, 새로운 위치를 추가할 때 중복을 피할 수 있습니다.
    """
    ) String getAllUserLocations() {
        List<String> locations = locationRepository.findByUserOrderByTagNameAsc(owner).stream().map(Location::getTagName).collect(Collectors.toList());
        return String.join(",\n", locations);
    }
}
