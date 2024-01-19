package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author cookie1551 Patrick_Star
 * @version 1.3
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProfessionServiceTests {
    @Autowired
    private ProfessionService professionService;

    @Test
    public void testFetchAllProfessionWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchAllProfession(null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllProfessionWithErrorType() {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchAllProfession(2);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllProfessionWithOk() {
        List<Profession> professionList = professionService.fetchAllProfession(3);
        professionList.forEach(profession -> assertEquals((Integer) 1, profession.getStoreId()));
    }
    @Test
    public void testFetchProfessionByProfessionIdWithNullUser() {
        Integer professionId = 1;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByProfessionId(null, professionId);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByProfessionIdWithNullProfessionId () {
        Integer userId = 3;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByProfessionId(userId, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByProfessionIdWithErrorType () {
        Integer userId = 2;
        Integer professionId = 1;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByProfessionId(userId, professionId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByProfessionIdWithErrorStoreId () {
        Integer userId = 33;
        Integer professionId = 1;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByProfessionId(userId, professionId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByProfessionIdWithOk () {
        Integer userId = 3;
        Integer professionId = 1;
        Profession profession = professionService.fetchProfessionByProfessionId(userId, professionId);
        assertEquals(professionId, profession.getId());
    }
    @Test
    public void testFetchProfessionByStoreIdAndManagerIdWithNullUser() {
        Integer userId = null;
        Integer storeId = 1;
        Integer managerId  = 4;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByStoreIdAndManagerId(userId, storeId, managerId);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByStoreIdAndManagerIdWithNullStoreId() {
        Integer userId = 3;
        Integer storeId = null;
        Integer managerId  = 4;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByStoreIdAndManagerId(userId, storeId, managerId);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByStoreIdAndManagerIdWithNullManagerId() {
        Integer userId = 3;
        Integer storeId = 1;
        Integer managerId  = null;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByStoreIdAndManagerId(userId, storeId, managerId);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByStoreIdAndManagerIdWithErrorType() {
        Integer userId = 2;
        Integer storeId = 1;
        Integer managerId  = 4;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByStoreIdAndManagerId(userId, storeId, managerId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByStoreIdAndManagerIdWithErrorStoreId() {
        Integer userId = 33;
        Integer storeId = 1;
        Integer managerId  = 4;
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.fetchProfessionByStoreIdAndManagerId(userId, storeId, managerId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchProfessionByStoreIdAndManagerIdWithOk() {
        Integer userId = 3;
        Integer storeId = 1;
        Integer managerId  = 4;
        Profession profession = professionService.fetchProfessionByStoreIdAndManagerId(userId, storeId, managerId);
        assertEquals(storeId, profession.getStoreId());
        assertEquals(managerId, profession.getManagerId());
    }

    @Test
    public void testAddProfessionWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.addProfession(null,3,User.Type.MANAGER);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddProfessionWithNullManagerId () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.addProfession(3,null,User.Type.MANAGER);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddProfessionWithNullType  () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.addProfession(3,3,null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddProfessionWithErrorType  () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.addProfession(2,3,User.Type.MANAGER);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testAddProfessionWithErrorManager  () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.addProfession(3,2,User.Type.MANAGER);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testAddProfessionWithOk() {
        professionService.addProfession(3,3,User.Type.MANAGER);
    }
    @Test
    public void testAddProfessionWithOkVice() {
        professionService.addProfession(3,4,User.Type.CASHIER);
    }

    @Test
    public void testUpdateProfessionWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.updateProfession(null,
                    Profession.builder()
                            .id(5)
                            .storeId(1)
                            .managerId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateProfessionWithNullProfessionId () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.updateProfession(3,
                    Profession.builder()
                            .id(null)
                            .storeId(1)
                            .managerId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateProfessionWithErrorType () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.updateProfession(3,
                    Profession.builder()
                            .id(5)
                            .storeId(1)
                            .managerId(2)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateProfessionWithErrorStoreId () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.updateProfession(33,
                    Profession.builder()
                            .id(5)
                            .storeId(1)
                            .managerId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateProfessionWithNullProfession () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.updateProfession(3,
                    Profession.builder()
                            .id(10)
                            .storeId(1)
                            .managerId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.Profession_NOT_EXISTED, exception.getCode());
    }

    @Test
    public void testUpdateProfessionWithOk() {
        professionService.updateProfession(3,
                Profession.builder()
                        .id(5)
                        .storeId(1)
                        .managerId(4)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }

    @Test
    public void testDeleteProfessionWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.deleteProfession(null, 5);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteProfessionWithNullProfessionId () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.deleteProfession(3, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteProfessionWithErrorType () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.deleteProfession(2, 5);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteProfessionWithErrorStoreId () {
        AppException exception = assertThrows(AppException.class, () -> {
            professionService.deleteProfession(33, 5);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteProfessionWithOk() {
        professionService.deleteProfession(3, 5);
    }
}
