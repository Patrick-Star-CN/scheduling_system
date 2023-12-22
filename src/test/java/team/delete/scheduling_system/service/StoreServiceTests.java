package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
    public void testAddStore() {
        Store store = Store.builder().storeId(2).address("1234").size(12.2).name("wcq").build();
        storeService.addStore(1, store);
    }

    @Test
    public void testDeleteStore() {
        storeService.deleteStore(1, 1);
    }

    @Test
    public void testFetchStore() {
        assertEquals(2, storeService.fetchAllStore(1).size());
    }

    @Test
    public void testUpdateStore() {
        Store store = Store.builder().storeId(1).address("1234567").size(13.2).name("wcq").build();
        storeService.updateStore(1, store);
    }
}
