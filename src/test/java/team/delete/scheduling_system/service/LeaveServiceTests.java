package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.dto.LeaveRecordDto;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.LeaveRecord;
import team.delete.scheduling_system.exception.AppException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LeaveServiceTests {
    @Autowired
    private LeaveRecordService leaveRecordService;

    @Test
    public void testFetchAllLeaveRecord() {
        Integer userId = 2;
        List<LeaveRecordDto> leaveRecordList = leaveRecordService.fetchAllLeaveRecord(userId);
        for (LeaveRecordDto leaveRecordDto : leaveRecordList) {
            System.out.println(leaveRecordDto);
        }
    }

    @Test
    public void testFindLeaveRecordByShift() {
        Integer userId = 2;
        LocalDate time = LocalDate.of(2023, 12, 30);
        Integer scheduleShift = 0;
        LeaveRecord leaveRecord = leaveRecordService.findLeaveRecordByShift(userId, time, scheduleShift);
        System.out.println(leaveRecord);
        assertEquals(userId, leaveRecord.getRequestPersonId());
    }

    @Test
    public void testFetchAllLeaveRecordByRange() {
        Integer userId = 2;
        LocalDate startTime = LocalDate.of(2022, 10, 11);
        LocalDate endTime = LocalDate.of(2024, 11, 11);
        List<LeaveRecord> leaveRecordList = leaveRecordService.fetchAllLeaveRecordByRange(userId, startTime, endTime);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testDeleteLeaveRecordByRecordId() {
        Integer requestPersonId = 2;
        Integer reviewerId = 7;
        leaveRecordService.deleteLeaveRecordByRecordId(requestPersonId, 7);
        leaveRecordService.deleteLeaveRecordByRecordId(requestPersonId, 11);
    }

    @Test
    public void testAddLeaveRecord() {
        Integer scheduleShift = 2;
        //普通员工添加
        int requestPersonId = 23;
        LocalDate leaveTime = LocalDate.of(2024, 1, 16);
        leaveRecordService.addLeaveRecord(requestPersonId, leaveTime, scheduleShift);
//        //小组长添加
//        requestPersonId = 7;
//        leaveTime = LocalDate.of(2024, 1, 15);
//        leaveRecordService.addLeaveRecord(requestPersonId, leaveTime, scheduleShift);
//        //副经理添加
//        leaveTime = LocalDate.of(2024, 1, 21);
//        leaveRecordService.addLeaveRecord(requestPersonId, leaveTime, scheduleShift);
    }

    @Test
    public void testAddLeaveRecordWithWrongSchedule() {
        Integer scheduleShift = 1;
        //普通员工添加
        int requestPersonId = 2;
        LocalDate leaveTime = LocalDate.of(2024, 1, 15);
        AppException appException = assertThrows(AppException.class, () -> {
            leaveRecordService.addLeaveRecord(requestPersonId, leaveTime, scheduleShift);
        });
        assertEquals(ErrorCode.USER_NOT_IN_SCHEDULE, appException.getCode());
    }

    @Test
    public void testAddLeaveRecordWithOnlyOne() {
        Integer scheduleShift = 1;
        //普通员工添加
        int requestPersonId = 2;
        LocalDate leaveTime = LocalDate.of(2024, 1, 15);
        AppException appException = assertThrows(AppException.class, () -> {
            leaveRecordService.addLeaveRecord(requestPersonId, leaveTime, scheduleShift);
        });
        assertEquals(ErrorCode.USER_CAN_NOT_LEAVE, appException.getCode());
    }

    @Test
    public void testDeleteLeaveRecordByTime() {
        Integer requestPersonId = 2;
        Integer scheduleShift = 1;
        LocalDate leaveTime = LocalDate.of(2024, 1, 2);
        leaveRecordService.deleteLeaveRecordByTime(requestPersonId, leaveTime, scheduleShift);
    }

    @Test
    public void testFetchAllReViewLeaveRecordByTime() {
        Integer userId = 7;
        LocalDate startTime = LocalDate.of(2022, 10, 11);
        LocalDate endTime = LocalDate.of(2024, 11, 11);
        List<LeaveRecord> leaveRecordList = leaveRecordService.fetchAllReViewLeaveRecordByTime(userId, startTime, endTime);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testFetchAllNeedReviewLeaveRecord() {
        Integer userId = 5;
        List<LeaveRecord> leaveRecordList = leaveRecordService.fetchAllNeedReviewLeaveRecord(userId);
        for (LeaveRecord leaveRecord : leaveRecordList) {
            System.out.println(leaveRecord);
        }
    }

    @Test
    public void testFetchAllReviewLeaveRecord() {
        Integer userId = 7;
        List<LeaveRecordDto> leaveRecordList = leaveRecordService.fetchAllReviewLeaveRecord(userId);
        for (LeaveRecordDto leaveRecordDto : leaveRecordList) {
            System.out.println(leaveRecordDto);
        }
    }

    @Test
    public void testReviewLeaveRecord() {
//        leaveRecordService.reviewLeaveRecord(userId, recordId, result);
        leaveRecordService.reviewLeaveRecord(5, 3, true);
    }

    @Test
    public void testReviewLeaveRecordWithNonExistentUser() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.reviewLeaveRecord(-1, 3, true));
        assertEquals(exception.getCode(), ErrorCode.USER_NOT_EXISTED);
    }

    @Test
    public void testReviewLeaveRecordWithNullUserIdOrRecordId() {
        AppException userIdNullException = assertThrows(AppException.class,
                () -> leaveRecordService.reviewLeaveRecord(null, 3, true));
        assertEquals(userIdNullException.getCode(), ErrorCode.PARAM_ERROR);

        AppException recordIdNullException = assertThrows(AppException.class,
                () -> leaveRecordService.reviewLeaveRecord(5, null, true));
        assertEquals(recordIdNullException.getCode(), ErrorCode.PARAM_ERROR);
    }

    @Test
    public void testReviewLeaveRecordWithNonExistentRecord() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.reviewLeaveRecord(5, -1, true));
        assertEquals(exception.getCode(), ErrorCode.LEAVE_RECORD_NOT_EXISTED);
    }

    @Test
    public void testReviewLeaveRecordWithPermissionError() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.reviewLeaveRecord(2, 12, true));
        assertEquals(exception.getCode(), ErrorCode.USER_PERMISSION_ERROR);
    }

    @Test
    public void testFetchAllReviewLeaveRecordWithNullUserId() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.fetchAllReviewLeaveRecord(null));
        assertEquals(exception.getCode(), ErrorCode.PARAM_ERROR);
    }

    @Test
    public void testFetchAllReviewLeaveRecordWithNoPermission() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.fetchAllReviewLeaveRecord(2));
        assertEquals(exception.getCode(), ErrorCode.USER_PERMISSION_ERROR);
    }

    @Test
    public void testAddLeaveRecordWithNullParams() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.addLeaveRecord(null, LocalDate.now(), 1));
        assertEquals(exception.getCode(), ErrorCode.PARAM_ERROR);

        exception = assertThrows(AppException.class,
                () -> leaveRecordService.addLeaveRecord(1, null, 1));
        assertEquals(exception.getCode(), ErrorCode.PARAM_ERROR);
    }

    @Test
    public void testAddLeaveRecordWithPastDate() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.addLeaveRecord(1, LocalDate.now().minusDays(1), 1));
        assertEquals(exception.getCode(), ErrorCode.ILLEGAL_LEAVE_TIME);
    }

    @Test
    public void testAddLeaveRecordWithUserCannotLeave() {
        AppException exception = assertThrows(AppException.class,
                () -> leaveRecordService.addLeaveRecord(10, LocalDate.now(), 1));
        assertEquals(exception.getCode(), ErrorCode.USER_CAN_NOT_LEAVE);
    }

}
