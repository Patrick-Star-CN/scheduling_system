package team.delete.scheduling_system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Case;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.constant.RegexPattern;
import team.delete.scheduling_system.dto.UserDto;
import team.delete.scheduling_system.entity.LeaveRecord;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.LeaveRecordMapper;
import team.delete.scheduling_system.mapper.ProfessionMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author YYHelen
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class LeaveRecordService {
    final LeaveRecordMapper leaveRecordMapper;
    final ProfessionMapper professionMapper;
    final UserMapper userMapper;
    /**
     * 查询自己的所有请假记录
     *
     * @param userId 请假人id
     * @return 请假人请假记录
     */
    public List<LeaveRecord> fetchAllLeaveRecord(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

       return leaveRecordMapper.selectLeaveRecordListByUserId(userId);
    }

    /**
     * 查询自己的特定请假记录
     *
     * @param userId 请假人id
     * @param leaveTime 申请的请假时间
     * @return 请假记录列表
     */
    public LeaveRecord findLeaveRecordByTime(Integer userId, LocalDate leaveTime) {
        if (userId == null||leaveTime==null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return leaveRecordMapper.selectLeaveRecordByDate(userId, leaveTime);
    }

    /**
     * 查询自己在某个时间区间请假记录
     *
     * @param userId 请假人id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 请假记录列表
     */
    public List<LeaveRecord> fetchAllLeaveRecordByRange(Integer userId, LocalDate startTime, LocalDate endTime) {
        if (userId == null||startTime==null||endTime==null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if(endTime.isBefore(startTime)){
            throw new AppException(ErrorCode.USELESS_TIME_RANGE);
        }
        return leaveRecordMapper.selectLeaveRecordListByRange(userId,startTime,endTime);
    }

    /**
     * 根据记录id删除未审核的记录
     *
     * @param userId 删除人id
     * @param recordId 请假记录id
     */
    public void deleteLeaveRecord(Integer userId,Integer recordId){
        if(userId==null||recordId==null){
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        LeaveRecord leaveRecord=leaveRecordMapper.selectLeaveRecordByRecordId(recordId);
        if(leaveRecord==null){
            throw new AppException(ErrorCode. LEAVE_RECORD_NOT_EXISTED);
        }
        Integer requestPersonId= leaveRecord.getRequestPersonId();
        Integer reviewerPersonID=leaveRecord.getReviewerPersonId();
        if(userId==reviewerPersonID||userId==requestPersonId){
            leaveRecordMapper.deleteById(leaveRecord);
        }
        else{
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 根据请假时间删除未审核的记录
     *
     * @param userId 删除人id
     * @param leaveTime 请假记录id
     */
    public void deleteLeaveRecordByTime (Integer userId,LocalDate leaveTime){
        if(userId==null||leaveTime==null){
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        LeaveRecord leaveRecord=findLeaveRecordByTime(userId,leaveTime);
        if(leaveRecord==null){
            throw new AppException(ErrorCode. LEAVE_RECORD_NOT_EXISTED);
        }
        Integer requestPersonId= leaveRecord.getRequestPersonId();
        Integer reviewerPersonID=leaveRecord.getReviewerPersonId();
        if(userId==reviewerPersonID||userId==requestPersonId){
            leaveRecordMapper.deleteById(leaveRecord);
        }
        else{
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 添加请假记录（申请请假）
     *
     * @param requestPersonId 用户id
     * @param leaveTime 请假人需要的请假时间
     */
    public void addLeaveRecord(Integer requestPersonId, LocalDate leaveTime){
        //输入参数不能为0
        if (requestPersonId == null||leaveTime==null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        LocalDate currentLocalDate = LocalDate.now(); // 获取当前的 LocalDate

        // 比较 leaveTime 是否为当天日期或当天之后的日期
        if (leaveTime.isBefore(currentLocalDate)) {
            throw new AppException(ErrorCode.ILLEGAL_LEAVE_TIME);
        }
        UserDto requestPerson=userMapper.selectUserByUserId(requestPersonId);
        if(requestPerson==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if(leaveRecordMapper.selectLeaveRecordByDate(requestPersonId,leaveTime)!=null) {
            throw new AppException(ErrorCode.REPEAT_LEAVE_RECORD);
        }
        String requestPersonType=requestPerson.getType();
        if(requestPersonType==null){
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }
        Integer storeId=requestPerson.getStoreId();
        Integer groupId=requestPerson.getGroupId();
        //筛选出来的用户信息必须完整
        if(storeId==null||groupId==null){
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }
        Integer reviewerId=null;
        //查找审核人
        switch (requestPersonType) {
            case "CASHIER":
            case "CUSTOMER_SERVICE":
            case "STORAGE":
                reviewerId= leaveRecordMapper.findGroupManagerIdByGroupId(groupId);
                break;
            case "GROUP_MANAGER":
                reviewerId= leaveRecordMapper.findViceManagerIdByStoreId(groupId, storeId);
                break;
            case "VICE_MANAGER":
                reviewerId= leaveRecordMapper.findStorageIdByStoreId(storeId);
                break;
            case "MANAGER":
                reviewerId= leaveRecordMapper.findSuperAdmin();
                break;
        }
        if(reviewerId==null){
            throw new AppException(ErrorCode.REVIEWER_NOT_EXISTED);
        }

        LeaveRecord build=LeaveRecord.builder().requestPersonId(requestPersonId)
                .reviewerPersonId(reviewerId)
                .leaveTime(leaveTime)
                .type(LeaveRecord.Type.NOT_PROCEED).build();

        leaveRecordMapper.insert(build);
    }

    /**
     * 查询需要审核的某个时间区间请假记录
     *
     * @param userId 请假人id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 请假记录列表
     */
    public List<LeaveRecord> fetchAllReViewLeaveRecordByTime(Integer userId, LocalDate startTime, LocalDate endTime) {
        if (userId == null||startTime==null||endTime==null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return leaveRecordMapper.selectReviewLeaveRecordListByTimeRange(userId,startTime,endTime);
    }

    /**
     * 查询需要审核的请假记录
     *
     * @param userId 审核人id
     * @return 请假记录列表
     */
    public List<LeaveRecord> fetchAllNeedReviewLeaveRecord(Integer userId){
        if(userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        UserDto userDto=userMapper.selectUserByUserId(userId);
        if(userDto==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String userType=userDto.getType();
        if(userType==null){
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }
        if(userType=="CASHIER"||userType=="CUSTOMER_SERVICE"||userType=="STORAGE"){
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return leaveRecordMapper.selectReviewLeaveRecordListByUserId(userId);
    }

    /**
     * 审核请假记录
     *
     * @param userId 审核人id
     * @param recordId 请假记录id
     * @param result 审核结果
     */
    public void reviewLeaveRecord(Integer userId,Integer recordId, boolean result){
        if(userId == null||recordId==null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if(userMapper.selectUserByUserId(userId)==null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        LeaveRecord leaveRecord=leaveRecordMapper.selectLeaveRecordByRecordId(recordId);
        if(leaveRecord==null){
            throw new AppException(ErrorCode. LEAVE_RECORD_NOT_EXISTED);
        }
                //检查有没有权限
        if(leaveRecord.getReviewerPersonId()!=userId){
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        LeaveRecord updateLeaveRecord=leaveRecord;
        if(result){
            updateLeaveRecord.setType(LeaveRecord.Type.PASS);
        }
        else{
            updateLeaveRecord.setType(LeaveRecord.Type.REJECT);
        }
        leaveRecordMapper.updateById(updateLeaveRecord);
        if(result){
            //更新排班表
        }


    }
}