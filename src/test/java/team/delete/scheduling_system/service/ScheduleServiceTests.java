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
public class ScheduleServiceTests {
    @Autowired
    private ScheduleService scheduleService;

    @Test
    public void testInitSchedule() {
        scheduleService.initSchedule(3, 1);
    }

    @Test
    public void testFetchSchedule() {
        assertEquals("1", scheduleService.fetchScheduleByDay(1, 1, 1).getStoreId());
    }

    @Test
    public void testFetchScheduleList() {
        assertEquals("1", scheduleService.fetchScheduleList(1, 1).get(0).getStoreId());
    }

    @Test
    public void testDeleteScheduleList() {
        scheduleService.deleteSchedule(3, 1);
    }

    @Test
    public void testFetchPersonalSchedule() {
//        assertEquals(2, scheduleService.fetchPersonalSchedule(2).get(0).size());
        System.out.printf(scheduleService.fetchPersonalSchedule(23).toString());
    }
}
