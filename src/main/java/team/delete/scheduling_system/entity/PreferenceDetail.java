package team.delete.scheduling_system.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PreferenceDetail {
    /**
     * 偏好类型
     */
    private Type type;
    /**
     * 时间段
     */
    private List<Integer> time;
    /**
     * 喜欢/不喜欢
     */
    private Boolean isLike;

    /**
     * 偏好类型
     */
    public enum Type{
        /**
         * 工作日偏好
         */
        WORKDAY,
        /**
         * 班次时长偏好
         */
        SHIFT
    }

}
