package team.delete.scheduling_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import team.delete.scheduling_system.entity.LeaveRecord;
import team.delete.scheduling_system.entity.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author YYHelen
 * @version 1.0
 */
@Mapper
public interface LeaveRecordMapper  extends BaseMapper<LeaveRecord> {
    @Select("SELECT * FROM user WHERE user.user_id = #{userId}")
    User selectUserListByUserId(Integer userId);

    @Select("SELECT * FROM leave_record WHERE leave_record.request_person_id = #{userId}")
    List<LeaveRecord> selectLeaveRecordListByUserId(Integer userId);

    @Select("SELECT * FROM leave_record WHERE leave_record.request_person_id = #{userId} " +
            "AND leave_record.leave_time = #{leaveTime}")
    LeaveRecord selectLeaveRecordByDate(Integer userId, LocalDate leaveTime);

    @Select("SELECT * FROM user_details_view WHERE user_id = #{userId}"+
            "AND leave_record.date = profession.type ")
    Integer findReviewerByUserId(Integer userId);

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User findUserByUserId(Integer userId);

    @Select("SELECT manager_id FROM profession " +
            "WHERE profession.store_id = #{storeId} AND type=(" +
            "SELECT type FROM group_tb WHERE id=#{groupId})")
    Integer findViceManagerIdByStoreId(Integer groupId, Integer storeId);


    @Select("SELECT manager_id FROM group_tb WHERE group_tb.id = #{groupId}")
    Integer findGroupManagerIdByGroupId(Integer groupId);

    @Select("SELECT user_id FROM user WHERE store_id = #{storeId} AND type = 'STORAGE'" )
    Integer findStorageIdByStoreId(Integer storeId);

    @Select("SELECT user_id FROM user WHERE type = 'SUPER_ADMIN'" )
    Integer findSuperAdmin();

    @Select("SELECT * FROM leave_record WHERE user_id=type = #{userId}")
    List<LeaveRecord> fetchLeaveRecordByReviewerId(Integer userId);

    @Select("SELECT * FROM leave_record WHERE record_id=#{recordId}")
    LeaveRecord selectLeaveRecordByRecordId(Integer recordId);

    @Select("SELECT * FROM leave_record WHERE reviewer_person_id= #{userId}")
    List<LeaveRecord> selectReviewLeaveRecordListByUserId(Integer userId);

    @Select("SELECT * FROM leave_record WHERE reviewer_person_id= #{userId} AND " +
            "leave_time BETWEEN '${startTime}' AND '${endTime}'")
    List<LeaveRecord> selectReviewLeaveRecordListByTimeRange(Integer userId, LocalDate startTime, LocalDate endTime);

    @Select("SELECT * FROM leave_record WHERE request_person_id = ${userId} AND " +
            "leave_time BETWEEN '${startTime}' AND '${endTime}'")
    List<LeaveRecord> selectLeaveRecordListByRange(Integer userId, LocalDate startTime, LocalDate endTime);

    void updateLeaveRecord(Integer recordId);
}
