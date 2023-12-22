package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.Group;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * @author cookie1551
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProfessionServiceTests {
    @Autowired
    private ProfessionService professionService;

    @Test
    public void testFetchAllProfession() {
        List<Profession> professionList = professionService.fetchAllProfession(2);
        professionList.forEach(profession -> {
            assertEquals((Integer) 1, profession.getStoreId());
        });
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
        professionService.addProfession(2,
                Profession.builder()
                        .storeId(1)
                        .managerId(1)
                        .type(Profession.Type.STORAGE).build());
    }

    @Test
    public void testUpdateProfession() {
        professionService.updateProfession(2,
                Profession.builder()
                        .id(4)
                        .storeId(1)
                        .managerId(2)
                        .type(Profession.Type.STORAGE).build());
    }

    @Test
    public void testDeleteProfession() {
        professionService.deleteProfession(2, 4);
    }
}
