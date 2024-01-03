package team.delete.scheduling_system.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerFlowServiceTests {
    @Autowired
    private CustomerFlowService customerFlowService;

    @Test
    public void testAddCustomerFlow() {
        customerFlowService.insertByExcel(1, new File("src/test/resources/客流数据.xlsx") );
    }
}
