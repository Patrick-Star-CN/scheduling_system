package team.delete.scheduling_system.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Builder
@ToString
@Document(collection = "shift")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Shift {
    /**
     * 班次id
     */
    @Id
    @MongoId
    @Field("shift_id")
    private String shiftId;
    /**
     * 门店id
     */
    @Field("store_id")
    private String storeId;
    /**
     * 员工排班
     */
    @Field("staff_shift")
    private List<ShiftDetail> staffShift;
    /**
     * 经理排班
     */
    @Field("manager_shift")
    private List<ShiftDetail> managerShift;
}
