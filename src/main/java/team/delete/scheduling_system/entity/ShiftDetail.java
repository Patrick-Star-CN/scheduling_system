package team.delete.scheduling_system.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShiftDetail {
    /**
     * 班次id
     */
    @Field("shift_id")
    private Integer shiftId;
    /**
     * 班次开始时间
     */
    @Field("start_time")
    private String startTime;
    /**
     * 班次结束时间
     */
    @Field("end_time")
    private String endTime;
    /**
     * 收银人数
     */
    @Field("cashier_count")
    private Integer cashierCount;
    /**
     * 导购人数
     */
    @Field("customer_service_count")
    private Integer customerServiceCount;
    /**
     * 库房人数
     */
    @Field("storage_count")
    private Integer storageCount;
}
