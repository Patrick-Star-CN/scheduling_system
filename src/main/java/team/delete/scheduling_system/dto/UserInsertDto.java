package team.delete.scheduling_system.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import team.delete.scheduling_system.entity.User;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInsertDto {
    /**
     * 用户名
     */
    String name;
    /**
     * 门店id
     */
    Integer storeId;
    /**
     * 小组id
     */
    Integer groupId;
    /**
     * 雇员职位
     */
    User.Type type;
}