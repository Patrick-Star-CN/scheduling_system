package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author cookie1551
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
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
     * 工种
     */
    User.Type type;
}
