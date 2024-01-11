package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.RuleDetail;
import team.delete.scheduling_system.entity.Schedule;
import team.delete.scheduling_system.entity.ScheduleDetail;
import team.delete.scheduling_system.exception.AppException;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author Devin100086
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChangeShiftServiceTests {
    @Autowired
    private ChangeShiftService changeShiftService;

    @Test
    public void testChangeShiftReplace() {
        changeShiftService.changeShift(3, 1, 13, 19, 0, null, 0, null);
    }

    @Test
    public void testChangeShiftWithDifferentType() {
        changeShiftService.changeShift(3, 1, 2, 13, 0, 0, 0, 6);
    }

    @Test
    public void testChangeShiftWithWrongUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            changeShiftService.changeShift(3, 1, 3, 13, 0, 0, 0, 6);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testChangeShiftWithSameDay() {
        changeShiftService.changeShift(3, 1, 19, 2, 0, 1, 0, 0);
    }
    @Test
    public void testChangeShiftWithDifferentRole(){
        AppException exception = assertThrows(AppException.class, () -> {
            changeShiftService.changeShift(3, 1, 2, 5, 0, 0, 0, 0);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }

    @Test
    public void testChangeShiftWithNoManager(){
        AppException exception = assertThrows(AppException.class, () -> {
            changeShiftService.changeShift(18, 1, 2, 5, 0, 0, 0, 0);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
}