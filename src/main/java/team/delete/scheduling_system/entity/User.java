package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class User {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    Integer userId;
    /**
     * 用户名
     */
    String username;
    /**
     * 密码
     */
    String password;
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
    Type type;

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
