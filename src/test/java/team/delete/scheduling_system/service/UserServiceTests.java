package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.dto.UserDto;
import team.delete.scheduling_system.dto.UserInsertDto;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author cookie1551 yyhelen Patrick_Star
 * @version 1.2
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        userService.addUser(1, null);
    }

    @Test
    public void testAddUserWithNullParams() {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.addUser(null, null);
        });
        assertEquals(exception.getCode(), ErrorCode.PARAM_ERROR);
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(1, 5);
    }

    @Test
    public void testFetchUserByUserIdWithNullUser() {
        AppException exception = assertThrows(AppException.class, () -> {
            UserDto userDto = userService.fetchUserDtoByUserId(null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchUserByUserIdWithOk() {
        Integer userId = 1;
        UserDto userDto = userService.fetchUserDtoByUserId(userId);
        assertEquals(userId, userDto.getUserId());
    }


    @Test
    public void testUpdateUserWithNullUserId () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUser(null,
                    User.builder()
                            .userId(15)
                            .name("customer_service1")
                            .storeId(1)
                            .groupId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateUserWithNullTypeArea  () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUser(1,null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testUpdateUserWithErrorTypeUser () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUser(1,
                    User.builder()
                            .userId(55)
                            .name("worker").build());
        });
        assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getCode());
    }
    @Test
    public void testUpdateUserWithOkAreaSys() {
        userService.updateUser(1,
                User.builder()
                        .userId(15)
                        .name("customer_service1")
                        .storeId(1)
                        .groupId(4)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }
    @Test
    public void testUpdateUserWithOkArea1() {
        userService.updateUser(3,
                User.builder()
                        .userId(15)
                        .name("customer_service1")
                        .storeId(1)
                        .groupId(4)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }
    @Test
    public void testUpdateUserWithErrorArea1() {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUser(33,
                    User.builder()
                            .userId(15)
                            .name("customer_service1")
                            .storeId(1)
                            .groupId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }

    @Test
    public void testUpdateUserWithOkArea2() {
        userService.updateUser(3,
                User.builder()
                        .userId(15)
                        .name("customer_service1")
                        .storeId(1)
                        .groupId(4)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }
    @Test
    public void testUpdateUserWithErrorArea2() {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUser(4,
                    User.builder()
                            .userId(2)
                            .name("cashier1")
                            .storeId(1)
                            .groupId(4)
                            .type(User.Type.CUSTOMER_SERVICE).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }

    @Test
    public void testUpdateUserWithOkArea3() {
        userService.updateUser(5,
                User.builder()
                        .userId(15)
                        .name("customer_service1")
                        .storeId(1)
                        .groupId(4).build());
    }
    @Test
    public void testUpdateUserWithErrorArea3() {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.updateUser(5,
                    User.builder()
                            .userId(21)
                            .name("customer_service5")
                            .storeId(1)
                            .groupId(5).build());
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }


    @Test
    public void testChangePasswordSuccess() {
        Integer userId = 1;
        String oldPassword = "Admin123";
        String newPassword = "Admin123!";
        userService.changePassword(userId, oldPassword, newPassword);
    }

    @Test
    public void testLogin() {
        assertEquals(User.Type.SUPER_ADMIN, userService.login("Admin", "Admin123"));
    }


    @Test
    public void testFetchAllUserWithNullUserId () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(null, 1, 1);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllUserWithNullTypeArea  () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(1, null, 1);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllUserWithNullTypeId () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(1, 1, null);
        });
        assertEquals(ErrorCode.PARAM_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllUserWithErrorType () {
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(2, 1, 1);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }
    @Test
    public void testFetchAllUserWithOkAreaSys() {
        Integer userId = 1;
        Integer typeArea = 1;
        Integer typeId = 1;
        List<User> userList = userService.fetchAllUser(userId, typeArea, typeId);
        switch (typeArea) {
            case 1:
                userList.forEach(user -> {
                    assertEquals(typeId, user.getStoreId());
                });
                break;
            case 2:
                userList.forEach(user -> {
                    assertEquals(User.Type.CASHIER, user.getType());
                });
                break;
            case 3:
                userList.forEach(user -> {
                    assertEquals(typeId, user.getGroupId());
                });
                break;
        }
    }
    @Test
    public void testFetchAllUserWithOkArea1() {
        Integer userId = 3;
        Integer typeArea = 1;
        Integer typeId = 1;
        List<User> userList = userService.fetchAllUser(userId, typeArea, typeId);
        userList.forEach(user -> {
            assertEquals(typeId, user.getStoreId());
        });
    }
    @Test
    public void testFetchAllUserWithErrorArea1() {
        Integer userId = 3;
        Integer typeArea = 1;
        Integer typeId = 2;
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(userId, typeArea, typeId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }

    @Test
    public void testFetchAllUserWithOkArea2() {
        Integer userId = 4;
        Integer typeArea = 2;
        Integer typeId = 3;
        List<User> userList = userService.fetchAllUser(userId, typeArea, typeId);
        userList.forEach(user -> {
            assertEquals(User.Type.CUSTOMER_SERVICE, user.getType());
        });
    }
    @Test
    public void testFetchAllUserWithErrorArea2() {
        Integer userId = 4;
        Integer typeArea = 2;
        Integer typeId = 4;
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(userId, typeArea, typeId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }

    @Test
    public void testFetchAllUserWithOkArea3() {
        Integer userId = 5;
        Integer typeArea = 3;
        Integer typeId = 4;
        List<User> userList = userService.fetchAllUser(userId, typeArea, typeId);
        userList.forEach(user -> {
            assertEquals(typeId, user.getGroupId());
        });
    }
    @Test
    public void testFetchAllUserWithErrorArea3() {
        Integer userId = 5;
        Integer typeArea = 3;
        Integer typeId = 5;
        AppException exception = assertThrows(AppException.class, () -> {
            userService.fetchAllUser(userId, typeArea, typeId);
        });
        assertEquals(ErrorCode.USER_PERMISSION_ERROR, exception.getCode());
    }



    @Test
    public void testFetchUserByGroup() {
        Integer userId = 5;
        List<UserDto> userList = userService.fetchUserByGroup(userId);
        userList.forEach(user -> {
            assertEquals((Object)4, user.getGroupId());
        });
    }

    @Test
    public void testAddCustomerFlow() {
        userService.insertByExcel(1, new File("src/test/resources/用户数据.xlsx") );
    }
}