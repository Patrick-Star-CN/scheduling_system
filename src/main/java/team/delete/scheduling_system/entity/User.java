package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

/**
 * @author Patrick_Star
 * @version 1.2
 */
@Data
@Builder
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
     * 雇员职位
     */
    Type type;

    enum Type {
        /**
         * 员工
         */
        STAFF,
        /**
         * 门店管理员
         */
        STORE_ADMIN,
        /**
         * 系统管理员
         */
        SUPER_ADMIN
    }
}
