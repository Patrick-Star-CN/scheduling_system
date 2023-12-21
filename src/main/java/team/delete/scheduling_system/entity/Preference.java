package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;


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
    private List<PreferenceDetail> preferenceDetail;
}
