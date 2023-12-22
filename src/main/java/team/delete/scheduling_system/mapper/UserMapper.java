package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.dto.UserDto;
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
            "    u.user_id, " +
            "    u.name, " +
            "    u.store_id, " +
            "    s.name AS store_name, " +
            "    s.address AS store_address, " +
            "    g.id AS group_id, " +
            "    u2.name AS group_manager_name, " +
            "    u1.name AS vice_manager_name, " +
            "    u.type " +
            "FROM " +
            "    user u " +
            "JOIN " +
            "    store s ON u.store_id = s.store_id " +
            "JOIN " +
            "    group_tb g ON u.group_id = g.id " +
            "JOIN " +
            "    profession p ON u.type = p.type AND u.store_id = p.store_id " +
            "JOIN " +
            "    user u1 ON u1.user_id = p.manager_id " +
            "JOIN " +
            "    user u2 ON u2.user_id = g.manager_id " +
            "WHERE " +
            "    u.user_id = #{userId};")
    UserDto selectUserByUserId(Integer userId);
}
