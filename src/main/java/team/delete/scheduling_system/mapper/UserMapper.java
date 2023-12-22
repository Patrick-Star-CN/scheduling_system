package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

    @Select("SELECT user.* FROM user JOIN profession ON profession.id = #{professionId} AND user.type = profession.type AND user.store_id = profession.store_id")
    List<User> selectUserListByProfession(Integer professionId);

    @Select("SELECT * FROM user WHERE user.group_id = #{groupId}")
    List<User> selectUserListByGroupId(Integer groupId);
}
