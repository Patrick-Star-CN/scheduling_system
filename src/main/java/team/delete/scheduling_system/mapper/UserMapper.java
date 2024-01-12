package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.dto.UserDto;
import team.delete.scheduling_system.dto.UserListDto;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Patrick_Star cookie1551
 * @version 1.1
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE user.store_id = #{storeId}")
    List<User> selectUserListByStoreId(Integer storeId);

    @Select("SELECT * FROM user, profession WHERE profession.id = #{professionId} " +
            "AND user.type = profession.type " +
            "AND user.store_id = profession.store_id")
    List<User> selectUserListByProfession(Integer professionId);

    @Select("SELECT * FROM user WHERE user.group_id = #{groupId}")
    List<User> selectUserListByGroupId(Integer groupId);

    @Select("SELECT " +
            "    * " +
            "FROM " +
            "    user_details_view u " +
            "WHERE " +
            "    u.user_id = #{userId};")
    UserDto selectUserByUserId(Integer userId);

    @Select("SELECT user.user_id, user.name, user.password, user.type, user.store_id, user.group_id FROM user, group_tb WHERE user.store_id = #{storeId} AND group_tb.type = #{type} AND user.group_id = group_tb.id")
    List<User> selectUserListByStoreIdAndType(Integer storeId, User.Type type);

    @Select("SELECT type FROM user_details_view u WHERE u.user_id = #{userId}")
    User.Type selectUserTypeByUserId(Integer userId);


    @Select("SELECT group_tb.type FROM user, group_tb WHERE user.user_id = #{userId} AND user.user_id = group_tb.manager_id")
    User.Type selectGroupTypeByUserId(Integer userId);

    @Select("SELECT user_details_view.* FROM user_details_view, group_tb WHERE user_details_view.store_id = #{storeId} AND group_tb.type = #{type} AND user_details_view.type = 'GROUP_MANAGER' AND user_details_view.user_id != #{userId}")
    List<UserDto> selectUserListByUserIdStoreIdAndGroupType(Integer userId, Integer storeId, User.Type type);

    @Select("SELECT user_details_view.* FROM user_details_view WHERE user_details_view.store_id = #{storeId} AND user_details_view.type = #{type}")
    List<UserDto> selectUserListByStoreIdAndUserType(Integer storeId, User.Type type);

    @Select("SELECT * FROM user_details_view u WHERE u.group_id = #{groupId} AND u.user_id != #{userId};")
    List<UserDto> selectUserListByGroup(Integer userId, Integer groupId);

    @Select("SELECT * FROM user_details_view u WHERE u.type = #{type} AND u.store_id = #{storeId};")
    List<UserDto> selectUserListByProfessionAndStoreId(User.Type type, Integer storeId);

    @Select("SELECT user_details_view.* FROM user_details_view,group_tb WHERE group_tb.type = #{type} AND user_details_view.store_id = #{storeId} AND group_tb.id = user_details_view.group_id AND user_details_view.type = 'GROUP_MANAGER';")
    List<UserDto> selectGroupManagerListByProfessionAndStoreId(User.Type type, Integer storeId);

    @Select("SELECT user_id, name FROM user;")
    List<UserListDto> selectUserListDto();
    @Select("SELECT * FROM user_details_view WHERE store_id = #{storeId} AND (type = 'CASHIER' OR type = 'STORAGE' OR type = 'CUSTOMER_SERVICE')")
    List<UserDto> selectUserListByStore(Integer storeId);
}
