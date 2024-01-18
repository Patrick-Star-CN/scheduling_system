package team.delete.scheduling_system.service;

import jakarta.validation.constraints.Null;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author Devin100086
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class StoreServiceTests {
    @Autowired
    StoreService storeService;

    @Test
    public void testAddStoreWithAdmin() {
        Store store = Store.builder().storeId(2).address("上海").size(12.2).name("零食店").build();
        storeService.addStore(1, store);
    }
    @Test
    public void testAddStoreWithNullUser() {
        Store store = Store.builder().storeId(2).address("1234").size(12.2).name("wcq").build();
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.addStore(null, store);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddStoreWithManager() {
        Store store = Store.builder().storeId(2).address("上海").size(12.2).name("零食店").build();
        storeService.addStore(3, store);
    }
    @Test
    public void testAddStoreWithWorker() {
        Store store = Store.builder().storeId(2).address("1234").size(12.2).name("wcq").build();
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.addStore(2, store);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testAddStoreWithNullStoreAndNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.addStore(null, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddStoreWithNullStoreAndAdmin() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.addStore(1, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddStoreWithNullStoreAndManager() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.addStore(3, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddStoreWithNullStoreAndWorker() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.addStore(2, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteStoreWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.deleteStore(null, 2);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteStoreWithAdmin() {
        storeService.deleteStore(1, 2);
    }
    @Test
    public void testDeleteStoreWithManager() {
        storeService.deleteStore(3, 2);
    }
    @Test
    public void testDeleteStoreWithWorker() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.deleteStore(2, 2);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteStoreWithNullStoreAndNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.deleteStore(null, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteStoreWithNullStoreAndAdmin() {
        storeService.deleteStore(1, null);
    }
    @Test
    public void testDeleteStoreWithNullStoreAndManager() {
        storeService.deleteStore(3, null);
    }
    @Test
    public void testDeleteStoreWithNullStoreAndWorker() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.deleteStore(2, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchStoreWithAdmin() {
        assertEquals(2, storeService.fetchAllStore(1).size());
    }
    @Test
    public void testFetchStoreWithManager() {
        assertEquals(1, storeService.fetchAllStore(3).size());
    }
    @Test
    public void testFetchStoreWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.fetchAllStore(null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchStoreWithOtherRole() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.fetchAllStore(2);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateStoreWithNullUser() {
        Store store = Store.builder().storeId(2).address("1234567").size(13.2).name("wcq").build();
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.updateStore(null, store);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateStoreWithAdmin() {
        Store store = Store.builder().storeId(2).address("1234567").size(13.2).name("wcq").build();
        storeService.updateStore(1, store);
    }
    @Test
    public void testUpdateStoreWithManager() {
        Store store = Store.builder().storeId(2).address("1234567").size(13.2).name("wcq").build();
        storeService.updateStore(3, store);
    }
    @Test
    public void testUpdateStoreWithWorker() {
        Store store = Store.builder().storeId(2).address("1234567").size(13.2).name("wcq").build();
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.updateStore(2, store);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateStoreWithNullStoreAndNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.updateStore(null, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateStoreWithNullStoreAndAdmin() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.updateStore(1, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateStoreWithNullStoreAndManager() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.updateStore(3, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateStoreWithNullStoreAndWorker() {
        AppException exception = assertThrows(AppException.class, () -> {
            storeService.updateStore(2, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
}
