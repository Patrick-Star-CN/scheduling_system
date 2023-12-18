package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.Preference;
import team.delete.scheduling_system.entity.PreferenceDetail;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Devin100086
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PreferenceServiceTests {
    @Autowired
    private PreferenceService preferenceService;

    @Test
    public void testAdd() {
        List<String> time = new ArrayList<>(List.of("10:00-12:00", "18:00-20:00"));
        List<Integer> workday = new ArrayList<>(List.of(1, 2, 3));
        PreferenceDetail preferenceDetail = PreferenceDetail.builder().
                time(time).
                workday(workday).
                build();
        Preference preference = Preference.builder().
                preferenceDetail(preferenceDetail).
                userId(2).
                build();
        preferenceService.addPreference(preference);
    }

    @Test
    public void deletePreference() {
        Integer userId = 2;
        preferenceService.deletePreferenceByUserId(userId);
    }

    @Test
    public void selectPreferenceByUserId() {
        Integer userId = 1;
        Preference preference = preferenceService.findPreferenceByUserId(userId);
        assertEquals(userId, preference.getUserId());
    }

    @Test
    public void updatePreference() {
        Integer userId = 1;
        List<String> time = new ArrayList<>(List.of("12:00-13:00", "16:00-28:00"));
        List<Integer> workday = new ArrayList<>(List.of(1, 3));
        PreferenceDetail preferenceDetail = PreferenceDetail.builder().
                time(time).
                workday(workday).
                build();
        preferenceService.updatePreference(userId, preferenceDetail);
    }
}
