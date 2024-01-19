package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Group;
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
public class GroupServiceTests {
    @Autowired
    private GroupService groupService;

    @Test
    public void testFetchAllGroupWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchAllGroup(null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllGroupWithErrorUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchAllGroup(2);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllGroupWithOk() {
        Integer userId = 3;
        List<Group> groupList = groupService.fetchAllGroup(userId);
        groupList.forEach(group -> {
            assertEquals((Integer) 1, group.getStoreId());
        });
    }
    @Test
    public void testFetchAllGroupWithOkVice() {
        Integer userId = 4;
        List<Group> groupList = groupService.fetchAllGroup(userId);
        groupList.forEach(group -> {
            assertEquals((Integer) 1, group.getStoreId());
        });
    }

    @Test
    public void testFetchGroupByGroupIdWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchGroupByGroupId(null, 4);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchGroupByGroupIdWithNullGroupId () {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchGroupByGroupId(4, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchGroupByGroupIdWithErrorUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchGroupByGroupId(2, 4);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchGroupByGroupIdWithErrorType() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchGroupByGroupId(4, 3);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchGroupByGroupIdWithErrorStoreId() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.fetchGroupByGroupId(33, 4);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchGroupByGroupIdWithOk() {
        Integer userId = 4;
        Integer groupId = 4;
        Group group = groupService.fetchGroupByGroupId(userId, groupId);
        assertEquals(groupId, group.getId());
    }

    @Test
    public void testFetchGroupByStoreIdAndManagerId() {
        List<Group> groupList = groupService.fetchGroupListByTypeAndStoreId(3, User.Type.STORAGE, 1);
        groupList.forEach(group -> assertEquals((Integer) 1, group.getStoreId()));
    }

    @Test
    public void testAddGroupWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.addGroupVice(null, 5, "1号店库房3组");
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddGroupWithNullManagerId () {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.addGroupVice(4, null, "1号店库房3组");
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddGroupWithNullName () {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.addGroupVice(4, 5, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testAddGroupWithErrorManagerId() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.addGroupVice(4, 3, "1号店库房3组");
        });
        assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getCode());
    }
    @Test
    public void testAddGroupWithErrorType() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.addGroupVice(2, 5, "1号店库房3组");
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testAddGroupWithOk() {
        groupService.addGroupVice(4, 5, "1号店库房3组");
    }

    @Test
    public void testUpdateGroupWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.updateGroup(null,
                    Group.builder()
                            .id(6)
                            .storeId(1)
                            .managerId(5)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateGroupWithNullGroupId () {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.updateGroup(4,
                    null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateGroupWithErrorUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.updateGroup(2,
                    Group.builder()
                            .id(4)
                            .storeId(1)
                            .managerId(5)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateGroupWithErrorStoreId() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.updateGroup(33,
                    Group.builder()
                            .id(4)
                            .storeId(1)
                            .managerId(5)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateGroupWithNullGroup() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.updateGroup(4,
                    Group.builder()
                            .id(16)
                            .storeId(1)
                            .managerId(5)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.GROUP_NOT_EXISTED, exception.getCode());
    }
    @Test
    public void testUpdateGroupWithOk() {
        groupService.updateGroup(4,
                Group.builder()
                        .id(6)
                        .storeId(1)
                        .managerId(5)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }

    @Test
    public void testDeleteGroupWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.deleteGroup(null, 6);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteGroupWithNullGroup () {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.deleteGroup(4, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteGroupWithErrorUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.deleteGroup(2, 6);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteGroupWithErrorStoreId() {
        AppException exception = assertThrows(AppException.class, () -> {
            groupService.deleteGroup(33, 6);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testDeleteGroupWithOk() {
        groupService.deleteGroup(4, 6);
    }
}
