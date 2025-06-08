package com.pnu.ailifelog.component;

import com.pnu.ailifelog.entity.DailySnapshot;
import com.pnu.ailifelog.entity.Snapshot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityTemplater {

    public String templateDailySnapshot(DailySnapshot dailySnapshot) {
        StringBuilder sb = new StringBuilder();
        sb.append("날짜: ").append(dailySnapshot.getDate()).append("\n");
        sb.append("일정 목록:\n");
        dailySnapshot.getSnapshots().forEach(snapshot -> {
            sb.append("- [").append(snapshot.getTimestamp().toLocalTime()).append("]: ").append(snapshot.getContent());
            if (snapshot.getLocation() != null) {
                sb.append(" (위치: ").append(snapshot.getLocation().getTagName());
                if (snapshot.getLocation().getLatitude() != null && snapshot.getLocation().getLongitude() != null) {
                    sb.append(", 위도: ").append(snapshot.getLocation().getLatitude())
                            .append(", 경도: ").append(snapshot.getLocation().getLongitude());
                }
            }
            sb.append("\n");
        });
        return sb.toString();
    }

    public String templateSnapShot(Snapshot snapshot) {
        StringBuilder sb = new StringBuilder();
        sb.append("-[").append(snapshot.getTimestamp().toString()).append("]: ").append(snapshot.getContent());
        if (snapshot.getLocation() != null) {
            sb.append(" (위치: ").append(snapshot.getLocation().getTagName());
            if (snapshot.getLocation().getLatitude() != null && snapshot.getLocation().getLongitude() != null) {
                sb.append(", 위도: ").append(snapshot.getLocation().getLatitude())
                        .append(", 경도: ").append(snapshot.getLocation().getLongitude());
            }
        }
        return sb.toString();
    }
}
