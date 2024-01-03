package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Profession {
    /**
     * 工种id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 主管id
     */
    private Integer managerId;
    /**
     * 工种
     */
    private User.Type type;
}
