package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.entity.Profession;

import java.util.List;

/**
 * @author cookie1551 Patrick_Star
 * @version 1.1
 */
@Mapper
public interface ProfessionMapper extends BaseMapper<Profession> {

    @Select("SELECT * FROM profession WHERE profession.store_id = #{storeId}")
    List<Profession> selectProfessionListByStoreId(Integer storeId);

    @Select("SELECT * FROM profession WHERE profession.store_id = #{storeId} AND profession.manager_id = #{managerId}")
    Profession selectProfessionByStoreIdAndManagerId(Integer storeId, Integer managerId);
}
