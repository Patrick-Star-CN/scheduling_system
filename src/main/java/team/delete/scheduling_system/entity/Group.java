package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author cookie1551 Patrick_Star
 * @version 1.1
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
@TableName("group_tb")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Group {
    /**
     * 小组id
     */
    @TableId(type = IdType.AUTO)
    Integer id;
    /**
     * 门店id
     */
    Integer storeId;
    /**
     * 主管id
     */
    Integer managerId;
    /**
     * 小组名
     */
    String name;
    /**
     * 工种
     */
    User.Type type;
}
