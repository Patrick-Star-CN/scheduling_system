package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;


/**
 * @author Devin100086
 * @version 1.0
 */
@Data
@Builder
@Document(collection = "preference")
@Accessors(chain = true)
public class Preference {
    /**
     * 偏好id
     */
    @MongoId
    @Field("preference_id")
    private String preferenceId;
    /**
     * 用户id
     */
    @Field("user_id")
    private Integer userId;
    /**
     *  偏好类型值
     */
    @Field("preference_detail")
    private PreferenceDetail preferenceDetail;

    /**
     * 偏好类型
     */
    public enum Type{
        /**
         * 工作日偏好
         */
        WORKDAY,
        /**
         * 工作时间偏好
         */
        TIME,
        /**
         * 班次时长偏好
         */
        SHIFT
    }
}
