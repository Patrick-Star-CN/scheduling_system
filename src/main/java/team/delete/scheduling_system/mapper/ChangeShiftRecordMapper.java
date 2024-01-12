package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.entity.ChangeShiftRecord;
import team.delete.scheduling_system.entity.LeaveRecord;

import java.util.List;

/**
 * @author Devin100086
 * @version 1.0
 */
@Mapper
public interface ChangeShiftRecordMapper extends BaseMapper<ChangeShiftRecord> {
    @Select("SELECT user_id FROM user WHERE store_id = #{storeId} AND type = 'MANAGER'" )
    Integer findStorageIdByStoreId(Integer storeId);
}
