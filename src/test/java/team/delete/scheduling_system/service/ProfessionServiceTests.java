package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author cookie1551 Patrick_Star
 * @version 1.1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProfessionServiceTests {
    @Autowired
    private ProfessionService professionService;

    @Test
    public void testFetchAllProfession() {
        List<Profession> professionList = professionService.fetchAllProfession(3);
        professionList.forEach(profession -> assertEquals((Integer) 1, profession.getStoreId()));
    }

    @Test
    public void testFetchProfessionByProfessionId() {
        Integer professionId = 1;
        Profession profession = professionService.fetchProfessionByProfessionId(2, professionId);
        assertEquals(professionId, profession.getId());
    }

    @Test
    public void testFetchProfessionByStoreIdAndManagerId() {
        Integer storeId = 1;
        Integer managerId  = 1;
        Profession profession = professionService.fetchProfessionByStoreIdAndManagerId(2, storeId, managerId);
        assertEquals(storeId, profession.getStoreId());
        assertEquals(managerId, profession.getManagerId());
    }

    @Test
    public void testAddProfession() {
        professionService.addProfession(3,18,User.Type.CASHIER);
    }

    @Test
    public void testUpdateProfession() {
        professionService.updateProfession(3,
                Profession.builder()
                        .id(1)
                        .storeId(1)
                        .managerId(2)
                        .type(User.Type.STORAGE).build());
    }

    @Test
    public void testDeleteProfession() {
        professionService.deleteProfession(2, 4);
    }
}
