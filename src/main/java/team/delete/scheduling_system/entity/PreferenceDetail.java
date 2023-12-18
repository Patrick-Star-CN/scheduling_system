package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Devin100086
 * @version 1.0
 */
@Data
@Builder
@Accessors(chain = true)
public class PreferenceDetail {
    /**
     * 工作日偏好值
     */
    private List<Integer> workday;
    /**
     * 工作时间偏好
     */
    private List<String> time;
    /**
     * 班次时间偏好
     */
    private Double shift;
}
