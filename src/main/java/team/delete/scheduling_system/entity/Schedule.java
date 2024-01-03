package team.delete.scheduling_system.entity;

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
@Document(collection = "schedule")
public class Schedule {
    /**
     * 班次id
     */
    @Id
    @MongoId
    @Field("schedule_id")
    private String scheduleId;
    /**
     * 门店id
     */
    @Field("store_id")
    private String storeId;
    /**
     * 一周中的第几天
     */
    @Field("week_id")
    private Integer weekId;
    /**
     * 员工排班
     */
    @Field("schedule_detail")
    private List<ScheduleDetail> scheduleDetails;
}
