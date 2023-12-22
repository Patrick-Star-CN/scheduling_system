package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.entity.Group;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;

import java.util.List;

/**
 * @author cookie1551
 * @version 1.0
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {
    @Select("SELECT * FROM group_db WHERE group_db.type = #{type} AND  group_db.store_id = #{storeId}")
    List<Group> selectGroupList(Profession.Type type, Integer storeId);
}
