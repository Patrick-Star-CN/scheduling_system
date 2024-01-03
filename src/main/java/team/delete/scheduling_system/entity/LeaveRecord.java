package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author YYHelen
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LeaveRecord {
    /**
     * 审核记录id
     */
    @TableId(type = IdType.AUTO)
    private Integer recordId;
    /**
     * 请假人id
     */
    private Integer requestPersonId;
    /**
     * 审核人id
     */
    private Integer reviewerPersonId;
    /**
     * 请假时间
     */
    private LocalDate leaveTime;
    /**
     * 请假审核完成时间
     */
    private Timestamp reviewTime;
    /**
     * 请假审核完成时间
     */
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
        REJECT;
    }
    Integer scheduleShift;
    /**
     * 请假审核状态
     */
}
