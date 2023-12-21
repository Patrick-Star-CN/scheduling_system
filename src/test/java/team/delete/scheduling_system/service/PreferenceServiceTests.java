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
        Integer userId = 1;
        PreferenceDetail preferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.TIME)
                .time(new ArrayList<>(List.of(8, 10)))
                .isLike(true)
                .build();
        Preference preference = Preference.builder()
                .userId(userId)
                .preferenceDetail(new ArrayList<>(List.of(preferenceDetail)))
                .build();
        preferenceService.addPreference(userId, preference);
    }

    @Test
    public void deletePreference() {
        preferenceService.deletePreferenceByUserId(1);
    }

    @Test
    public void selectPreferenceByUserId() {
        Integer userId = 1;
        Preference preference = preferenceService.findPreferenceByUserId(userId);
        assertEquals(userId, preference.getUserId());
    }

    @Test
    public void updatePreference() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.TIME)
                .time(new ArrayList<>(List.of(8, 11)))
                .isLike(true)
                .build();
        PreferenceDetail oldPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.TIME)
                .time(new ArrayList<>(List.of(8, 10)))
                .isLike(true)
                .build();

        preferenceService.updatePreference(1, oldPreferenceDetail, newPreferenceDetail);
    }
}
