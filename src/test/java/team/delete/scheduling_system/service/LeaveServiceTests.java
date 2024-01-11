package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.dto.LeaveRecordDto;
import team.delete.scheduling_system.entity.LeaveRecord;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LeaveServiceTests {
    @Autowired
    private LeaveRecordService leaveRecordService;

    @Test
    public void testFetchAllLeaveRecord(){
        Integer userId=2;
        List<LeaveRecordDto> leaveRecordList=leaveRecordService.fetchAllLeaveRecord(userId);
        for (LeaveRecordDto leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testFindLeaveRecordByShift(){
        Integer userId=2;
        LocalDate time=LocalDate.of(2023, 12,30);
        Integer scheduleShift=0;
        LeaveRecord leaveRecord=leaveRecordService.findLeaveRecordByShift(userId,time,scheduleShift);
        System.out.println(leaveRecord);
        assertEquals(userId,leaveRecord.getRequestPersonId());
    }

    @Test
    public void testFetchAllLeaveRecordByRange(){
        Integer userId=2;
        LocalDate startTime=LocalDate.of(2022, 10, 11);
        LocalDate endTime=LocalDate.of(2024, 11, 11);
        List<LeaveRecord> leaveRecordList=leaveRecordService.fetchAllLeaveRecordByRange(userId,startTime,endTime);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testDeleteLeaveRecordByRecordId(){
        Integer requestPersonId=2;
        Integer reviewerId=7;
        leaveRecordService.deleteLeaveRecordByRecordId(requestPersonId,7);
        leaveRecordService.deleteLeaveRecordByRecordId(requestPersonId,11);
    }

    @Test
    public void testAddLeaveRecord(){
        Integer scheduleShift=1;
        //普通员工添加
        Integer requestPersonId=2;
        LocalDate leaveTime=LocalDate.of(2024,1,2);
        leaveRecordService.addLeaveRecord(requestPersonId,leaveTime,scheduleShift);
        //小组长添加
        requestPersonId=7;
        leaveTime=LocalDate.of(2024,1,15);
        leaveRecordService.addLeaveRecord(requestPersonId,leaveTime,scheduleShift);
        //副经理添加
        requestPersonId=7;
        leaveTime=LocalDate.of(2024,1,21);
        leaveRecordService.addLeaveRecord(requestPersonId,leaveTime,scheduleShift);
    }

    @Test
    public void testDeleteLeaveRecordByTime(){
        Integer requestPersonId=2;
        Integer scheduleShift=1;
        LocalDate leaveTime=LocalDate.of(2024,1,2);
        leaveRecordService.deleteLeaveRecordByTime(requestPersonId,leaveTime,scheduleShift);
    }

    @Test
    public void testFetchAllReViewLeaveRecordByTime(){
        Integer userId=7;
        LocalDate startTime=LocalDate.of(2022, 10, 11);
        LocalDate endTime=LocalDate.of(2024, 11, 11);
        List<LeaveRecord> leaveRecordList=leaveRecordService.fetchAllReViewLeaveRecordByTime(userId,startTime,endTime);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testFetchAllNeedReviewLeaveRecord(){
        Integer userId=7;
        List<LeaveRecord> leaveRecordList=leaveRecordService.fetchAllNeedReviewLeaveRecord(userId);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testFetchAllReviewLeaveRecord(){
        Integer userId=7;
        List<LeaveRecordDto> leaveRecordList=leaveRecordService.fetchAllReviewLeaveRecord(userId);
        for (LeaveRecordDto leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testReviewLeaveRecord(){
        boolean result=false;
        Integer userId=7;
        Integer recordId=15;
        leaveRecordService.reviewLeaveRecord(userId,recordId,result);
        leaveRecordService.reviewLeaveRecord(userId,16,!result);
    }


}
