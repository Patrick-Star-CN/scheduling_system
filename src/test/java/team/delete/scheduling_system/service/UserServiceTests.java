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
        userService.addUser(1,
                UserInsertDto.builder()
                        .name("test")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.CASHIER).build());
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
    public void testFetchUserByUserId() {
        Integer userId = 1;
        UserDto userDto = userService.fetchUserDtoByUserId(userId);
        System.out.println(userDto);
        assertEquals(userId, userDto.getUserId());
    }

    @Test
    public void testUpdateUser() {
        userService.updateUser(1,
                User.builder()
                        .userId(5)
                        .name("王五")
                        .password("12345678")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.CUSTOMER_SERVICE).build());
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
    public void testFetchAllUser() {
        Integer userId = 1;
        Integer typeArea = 3;
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
}