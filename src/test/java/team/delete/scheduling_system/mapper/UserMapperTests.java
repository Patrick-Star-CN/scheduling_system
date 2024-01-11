package team.delete.scheduling_system.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.User;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserMapperTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testFetchUsersByStoreIdAndType() {
        System.out.println(userMapper.selectUserListByStoreIdAndType(1, User.Type.STORAGE));
    }

    @Test
    public void testFetchUserByUserId() {
        System.out.println(userMapper.selectUserListByStoreId(1));
    }
    @Test
    public void selectUserTypeByUserId() {
        System.out.println(userMapper.selectUserTypeByUserId(1));
    }
}
