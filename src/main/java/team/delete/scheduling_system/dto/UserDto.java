package team.delete.scheduling_system.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private Integer userId;
    private String name;
    private Integer storeId;
    private String storeName;
    private String storeAddress;
    private Integer groupId;
    private String groupName;
    private String groupManagerName;
    private String managerName;
    private String type;
}
