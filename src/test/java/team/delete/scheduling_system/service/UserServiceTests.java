package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.User;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author cookie1551 yyhelen Patrick_Star
 * @version 1.1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        userService.addUser(1,
                User.builder()
                        .userId(5)
                        .name("李四")
                        .password("12345678")
                        .storeId(1)
                        .groupId(1)
                        .type(User.Type.CUSTOMER_SERVICE).build());
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(1, 5);
    }

    @Test
    public void testFetchUserByUserId() {
        Integer userId = 1;
        User user = userService.fetchUserByUserId(userId);
        assertEquals(userId, user.getUserId());
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
        userService.login("admin", "Admin123");
    }

//    @Test
//    public void testFetchAllUser() {
//        Integer userId = 1;
//        Integer typeArea = 3;
//        Integer typeId = 1;
//        List<User> userList = userService.fetchAllUser(userId, typeArea, typeId);
//        switch (typeArea) {
//            case 1:
//                userList.forEach(user -> {
//                    assertEquals(typeId, user.getStoreId());
//                });
//            case 2:
//                userList.forEach(user -> {
//                    assertEquals("CASHIER", user.getType());
//                });
//            case 3:
//                userList.forEach(user -> {
//                    assertEquals(typeId, user.getGroupId());
//                });
//        }
//    }
}