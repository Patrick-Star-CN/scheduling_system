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

import static org.junit.Assert.assertEquals;

/**
 * @author cookie1551 Patrick_Star
 * @version 1.3
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GroupServiceTests {
    @Autowired
    private GroupService groupService;

    @Test
    public void testAddGroup() {
        groupService.addGroup(6, 5, "1号店库房2组");
    }

    @Test
    public void testDeleteGroup() {
        groupService.deleteGroup(6, 8);
    }

    @Test
    public void testUpdateGroup() {
        groupService.updateGroup(6,
                Group.builder()
                        .id(8)
                        .storeId(1)
                        .managerId(5)
                        .type(User.Type.STORAGE).build());
    }

    @Test
    public void testFetchGroupByGroupId() {
        Integer userId = 6;
        Integer groupId = 3;
        Group group = groupService.fetchGroupByGroupId(userId, groupId);
        assertEquals(groupId, group.getId());
    }

    @Test
    public void testFetchAllGroup() {
        Integer userId = 6;
        List<Group> groupList = groupService.fetchAllGroup(userId);
        groupList.forEach(group -> {
            assertEquals((Integer) 1, group.getStoreId());
        });
    }

    @Test
    public void testFetchGroupByStoreIdAndManagerId() {
        List<Group> groupList = groupService.fetchGroupListByTypeAndStoreId(3, User.Type.STORAGE, 1);
        groupList.forEach(group -> assertEquals((Integer) 1, group.getStoreId()));
    }
}
