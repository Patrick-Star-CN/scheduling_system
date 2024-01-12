package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.entity.Group;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;

import java.util.List;

/**
 * @author cookie1551 Patrick_Star
 * @version 1.1
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {
    @Select("SELECT * FROM group_tb WHERE group_tb.type = #{type} AND  group_tb.store_id = #{storeId}")
    List<Group> selectGroupList(User.Type type, Integer storeId);

    @Select("SELECT * FROM group_tb WHERE group_tb.store_id = #{storeId}")
    List<Group> selectGroupListByStoreId(Integer storeId);

    @Select("SELECT * FROM group_tb WHERE group_tb.manager_id = #{managerId}")
    Group selectGroupByManagerId(Integer managerId);
    @Select("SELECT * FROM group_tb WHERE group_tb.store_id = #{storeId} AND group_tb.type != 'MANAGER'")
    List<Group> selectGroupListByStoreIdM(Integer storeId);
}
