package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.User;

import static org.junit.Assert.assertEquals;

/**
 * @author cookie1551 yyhelen
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        userService.addUser(User.builder()
                        .userId(1)
                        .username("li")
                        .password("NewPassword123!")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.MANAGER).build(),
                User.builder()
                        .userId(5)
                        .username("李四")
                        .password("12345678")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.GROUP_MANAGER).build());
    }

    @Test
    public void testDeleteUser() {
        Integer userId = 5;
        userService.deleteUser(User.builder()
                .userId(1)
                .username("li")
                .password("NewPassword123!")
                .storeId(1)
                .groupId(1)
                .type(User.Type.MANAGER).build(),userId);
    }

    @Test
    public void testSelectUserByUserId() {
        Integer userId = 1;
        User user = userService.fetchUserByUserId(userId);
        assertEquals(userId, user.getUserId());
    }

    @Test
    public void testUpdateUser() {
        userService.updateUser(User.builder()
                        .userId(1)
                        .username("li")
                        .password("NewPassword123!")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.MANAGER).build(),
                User.builder()
                        .userId(5)
                        .username("王五")
                        .password("12345678")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }

    @Test
    public void testChangePasswordSuccess() {
        Integer userId = 1;
        String oldPassword = "oldPassword";
        String newPassword = "NewPassword123!";
        userService.changePassword(userId, oldPassword, newPassword);
    }

    @Test
    public void testLogin() {
        User user = User.builder()
                .userId(1)
                .username("li")
                .password("NewPassword123!")
                .storeId(1)
                .groupId(1)
                .type(User.Type.MANAGER)
                .build();
        userService.login("li", "NewPassword123!");
    }
}