package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Preference;
import team.delete.scheduling_system.entity.PreferenceDetail;
import team.delete.scheduling_system.exception.AppException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author Devin100086 Patrick_Star
 * @version 1.2
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class PreferenceServiceTests {
    @Autowired
    private PreferenceService preferenceService;

    @Test
    public void testAddWithNullUserAndNullDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.addPreference(null, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void testAddWithNullUser() {
        PreferenceDetail preferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(1, 2)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.addPreference(null, preferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void testAddWithNullDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.addPreference(1, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void testAddWithOk() {
        PreferenceDetail preferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(1, 2)))
                .isLike(true)
                .build();
        preferenceService.addPreference(1, preferenceDetail);
    }

    @Test
    public void testAddWithFalse() {
        PreferenceDetail preferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(1, 2, 2)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.addPreference(1, preferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void deletePreferenceWithNullUserAndNullDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.deletePreferenceByUserId(null, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void deletePreferenceWithNullDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.deletePreferenceByUserId(1, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void deletePreferenceWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.deletePreferenceByUserId(null, 0);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void deletePreferenceWithSmallDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.deletePreferenceByUserId(1, -1);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void deletePreferenceWithBigDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.deletePreferenceByUserId(1, 100);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void deletePreferenceWithDetailAndUser() {
        preferenceService.deletePreferenceByUserId(1, 0);
    }

    @Test
    public void selectPreferenceByUserId() {
        Integer userId = 1;
        Preference preference = preferenceService.findPreferenceByUserId(userId);
        assertEquals(userId, preference.getUserId());
    }

    @Test
    public void selectPreferenceWithNullUserId() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.findPreferenceByUserId(null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }

    @Test
    public void updatePreferenceWithAllNull() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(null, null, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithNullUserAndNullOldDetail() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(3, 4)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(null, null, newPreferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithNullUserAndNullNewDetail() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(null, 0, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithNullUser() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(3, 4)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(null, -1, newPreferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithNullOldAndNullNew() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(3, 4)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(1, null, newPreferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithNullNew() {
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(1, 100, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithBadDetail() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(3, 4,3)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(1, 0, newPreferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreferenceWithGoodDetail() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(3, 4,3)))
                .isLike(true)
                .build();
        AppException exception = assertThrows(AppException.class, () -> {
            preferenceService.updatePreference(1, 0, newPreferenceDetail);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void updatePreference() {
        PreferenceDetail newPreferenceDetail = PreferenceDetail.builder()
                .type(PreferenceDetail.Type.WORKDAY)
                .time(new ArrayList<>(List.of(3, 4)))
                .isLike(true)
                .build();

        preferenceService.updatePreference(1, 0, newPreferenceDetail);
    }
}
