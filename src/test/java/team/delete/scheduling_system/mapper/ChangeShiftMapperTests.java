package team.delete.scheduling_system.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.User;

/**
 * @author Devin100086
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChangeShiftMapperTests {
    @Autowired
    private ChangeShiftRecordMapper changeShiftRecordMapper;
    @Test
    public void testSelectChangeRecordByRecordId() {
        System.out.println(changeShiftRecordMapper.selectById(3));
    }
}
