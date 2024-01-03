package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ShiftServiceTests {
    @Autowired
    private ShiftService shiftService;

    @Test
    public void testAddShift() {
        shiftService.initShift(3, 1);
    }

    @Test
    public void testDeleteShift() {
        assertEquals("1", shiftService.fetchShift(1, 1).getStoreId());
    }

    @Test
    public void testInitSchedule() {
        shiftService.initSchedule(3, 1);
    }
}
