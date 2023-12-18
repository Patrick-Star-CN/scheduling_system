package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

/**
 * @author Devin100086
 * @version 1.0
 */

@Data
@Builder
public class Store {
    /**
     * 门店id
     */
    @TableId(type = IdType.AUTO)
    private Integer storeId;
    /**
     * 门店名字
     */
    private String name;
    /**
     * 门店位置
     */
    private String address;
    /**
     * 占地面积
     */
    private Double size;
}
