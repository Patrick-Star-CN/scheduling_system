package team.delete.scheduling_system.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Builder
@ToString
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LeaveRecordDto {
    private Integer recordId;
    private String requestPerson;
    private String reviewerPerson;
    private String leaveTime;
    private String reviewTime;
    private String type;
    private Integer scheduleShift;

    // Getters and Setters
}
