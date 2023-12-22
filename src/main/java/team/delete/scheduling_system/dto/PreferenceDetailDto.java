package team.delete.scheduling_system.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import team.delete.scheduling_system.entity.PreferenceDetail;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PreferenceDetailDto {
    private Integer oldPreferenceDetailId;
    private PreferenceDetail newPreferenceDetail;
}
