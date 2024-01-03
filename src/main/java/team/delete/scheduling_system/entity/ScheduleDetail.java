package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Field;
import team.delete.scheduling_system.dto.UserScheduleDto;

import java.util.List;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
public class ScheduleDetail {
    /**
     * 班次id
     */
    @Field("shift_id")
    private Integer shiftId;
    /**
     * 收银人
     */
    @Field("cashier_list")
    private List<UserScheduleDto> cashierList;
    /**
     * 导购人数
     */
    @Field("customer_service_list")
    private List<UserScheduleDto> customerServiceList;
    /**
     * 库房人数
     */
    @Field("storage_list")
    private List<UserScheduleDto> storageList;
}
