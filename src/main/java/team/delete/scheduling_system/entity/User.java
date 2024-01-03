package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Patrick_Star cookie1551
 * @version 1.3
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;
    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * 雇员职位
     */
    private Type type;
    /**
     * 门店id
     */
    private Integer storeId;
    /**
     * 小组id
     */
    private Integer groupId;


    public enum Type {
        /**
         * 收银
         */
        CASHIER,
        /**
         * 导购
         */
        CUSTOMER_SERVICE,
        /**
         * 库房
         */
        STORAGE,
        /**
         * 门店经理
         */
        MANAGER,
        /**
         * 副经理
         */
        VICE_MANAGER,
        /**
         * 小组长
         */
        GROUP_MANAGER,
        /**
         * 系统管理员
         */
        SUPER_ADMIN
    }
}
