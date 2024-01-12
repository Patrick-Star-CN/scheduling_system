package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Devin100086
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
@TableName("change_shift_record")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChangeShiftRecord {
    /**
     * 换班id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 换班人id
     */
    @Field("request_person_id")
    private Integer requestPersonId;
    /**
     * 被换班人id
     */
    @Field("reviewer_person_id")
    private Integer reviewerPersonId;
    /**
     * 原先日期
     */
    @Field("week_id1")
    private Integer weekId1;
    /**
     * 换班日期
     */
    @Field("week_id2")
    private Integer weekId2;
    /**
     * 原先班次
     */
    @Field("shift_id1")
    private Integer shiftId1;
    /**
     * 换班班次
     */
    @Field("shift_id2")
    private Integer shiftId2;
    /**
     * 换班审核状态
     */
    @Field("type")
    private Type type;
    public enum Type {
        /**
         * 未审核
         */
        NOT_PROCEED,
        /**
         * 审核通过
         */
        PASS,
        /**
         * 审核拒绝
         */
        REJECT
    }
}
