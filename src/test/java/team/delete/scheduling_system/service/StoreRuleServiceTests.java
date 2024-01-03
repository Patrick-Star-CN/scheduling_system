package team.delete.scheduling_system.service;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import team.delete.scheduling_system.entity.Rule;
import team.delete.scheduling_system.entity.RuleDetail;
import team.delete.scheduling_system.entity.User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StoreRuleServiceTests {
    @Autowired
    private StoreRuleService storeRuleService;

    @Test
    public void testInsertRule() {
        storeRuleService.insertRule(3, Rule.builder()
                .storeId(1)
                .closeStore(RuleDetail.builder()
                        .count(5)
                        .formula(3.5)
                        .time(90).build())
                .openStore(RuleDetail.builder()
                        .count(5)
                        .formula(3.5)
                        .time(90).build())
                .passenger(RuleDetail.builder()
                        .count(5)
                        .formula(3.5)
                        .time(0).build()).build());
    }

    @Test
    public void testUpdateRule() {
        Map<Integer, RuleDetail> ruleDetailMap = new HashMap<>();
        ruleDetailMap.put(1, RuleDetail.builder()
                .count(1)
                .formula(2.5)
                .time(60).build());

        storeRuleService.updateRule(1, ruleDetailMap,2);
    }

    @Test
    public void testGetRule() {
        assertEquals(1, storeRuleService.fetchRule(3, 1).getStoreId().intValue());
    }

    @Test
    public void testRemoveRule() {
        storeRuleService.deleteRule(1, "65831f40e9ddcc228fbb6e08");
    }
}
