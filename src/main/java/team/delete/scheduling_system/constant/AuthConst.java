package team.delete.scheduling_system.constant;

/**
 * 身份权限枚举类
 *
 * @author patrick_star
 * @version 1.0
 */
public class AuthConst {
    /**
     * 私有构造方法
     */
    private AuthConst() {
    }

    // --------------- 代表身份的权限 ---------------

    /**
     * 角色_id 管理员
     * 拥有管理所有门店权限
     */
    public static final String SUPER_ADMIN = "super_admin";

    /**
     * 角色_id 门店经理
     * 拥有管理单一门店权限
     */
    public static final String MANAGER = "manager";

    /**
     * 角色_id 副经理
     * 拥有管理门店某一工种权限
     */
    public static final String VICE_MANAGER = "vice_manager";

    /**
     * 角色_id 小组长
     * 拥有管理门店某一小组权限
     */
    public static final String GROUP_MANAGER = "group_manager";

    /**
     * 角色_id 员工
     * 拥有基本权限
     */
    public static final String EMPLOYEE = "employee";

    // --------------- 所有权限码 ---------------

    // ----------------项目关联的额权限码------------

}
