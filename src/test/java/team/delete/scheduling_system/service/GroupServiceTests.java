package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.Group;
import team.delete.scheduling_system.entity.Profession;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author cookie1551
 * @version 1.1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GroupServiceTests {
    @Autowired
    private GroupService groupService;

    @Test
    public void testAddGroup() {
        groupService.addGroup(3,
                Group.builder()
                        .storeId(1)
                        .managerId(2)
                        .type(Profession.Type.CASHIER).build());
    }

    @Test
    public void testDeleteGroup() {
        groupService.deleteGroup(3, 7);
    }

    @Test
    public void testUpdateGroup() {
        groupService.updateGroup(3,
                Group.builder()
                        .id(2)
                        .storeId(1)
                        .managerId(3)
                        .type(Profession.Type.CASHIER).build());
    }

    @Test
    public void testFetchGroupByGroupId() {
        Integer userId = 3;
        Integer groupId = 1;
        Group group = groupService.fetchGroupByGroupId(userId, groupId);
        assertEquals(groupId, group.getId());
    }

    @Test
    public void testFetchAllGroup() {
        Integer userId = 3;
        List<Group> groupList = groupService.fetchAllGroup(userId);
        groupList.forEach(group -> {
            assertEquals((Integer) 1, group.getStoreId());
        });
    }
}
