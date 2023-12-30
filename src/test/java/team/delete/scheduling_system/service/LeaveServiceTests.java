package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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
        List<LeaveRecord> leaveRecordList=leaveRecordService.fetchAllLeaveRecord(userId);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testFindLeaveRecordByTime(){
        Integer userId=2;
        LocalDate time=LocalDate.of(2023, 10, 11);
        LeaveRecord leaveRecord=leaveRecordService.findLeaveRecordByTime(userId,time);
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
    public void testDeleteLeaveRecord(){
        Integer requestPersonId=2;
        Integer reviewerId=7;
        leaveRecordService.deleteLeaveRecord(requestPersonId,2);
        leaveRecordService.deleteLeaveRecord(requestPersonId,4);
    }

    @Test
    public void testAddLeaveRecord(){
        //普通员工添加
        Integer requestPersonId=2;
        LocalDate leaveTime=LocalDate.of(2024,1,2);
        leaveRecordService.addLeaveRecord(requestPersonId,leaveTime);
        //小组长添加
        requestPersonId=7;
        leaveTime=LocalDate.of(2024,1,15);
        leaveRecordService.addLeaveRecord(requestPersonId,leaveTime);
        //副经理添加
        requestPersonId=7;
        leaveTime=LocalDate.of(2024,1,21);
        leaveRecordService.addLeaveRecord(requestPersonId,leaveTime);
    }

    @Test
    public void testDeleteLeaveRecordByTime(){
        //普通员工添加
        Integer requestPersonId=2;
        LocalDate leaveTime=LocalDate.of(2023,12,31);
        leaveRecordService.deleteLeaveRecordByTime(requestPersonId,leaveTime);
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
    public void testReviewLeaveRecord(){
        boolean result=false;
        Integer userId=7;
        Integer recordId=11;
        leaveRecordService.reviewLeaveRecord(userId,recordId,result);
    }


}
