package team.delete.scheduling_system.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.JsonAdapter;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Builder
@ToString
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LeaveRequestDto {
    private LocalDate leaveTime;
    @JsonAdapter(LocalDateAdapter.class)
    private Integer scheduleShift;

    // Getters and Setters
}
