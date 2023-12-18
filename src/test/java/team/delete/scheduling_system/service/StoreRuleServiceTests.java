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
        storeRuleService.insertRule(1, Rule.builder()
                .storeId(1)
                .closeStore(RuleDetail.builder()
                        .count(1)
                        .formula(3.5)
                        .time(1.5).build())
                .openStore(RuleDetail.builder()
                        .count(1)
                        .formula(3.5)
                        .time(1.5).build())
                .passenger(RuleDetail.builder()
                        .count(1)
                        .formula(3.5)
                        .time(1.5).build()).build());
    }

    @Test
    public void testUpdateRule() {
        Map<Integer, RuleDetail> ruleDetailMap = new HashMap<>();
        ruleDetailMap.put(1, RuleDetail.builder()
                .count(1)
                .formula(2.5)
                .time(4.5).build());

        storeRuleService.updateRule(1, ruleDetailMap,1);
    }

    @Test
    public void testGetRule() {
        assertEquals("6580746c73981656f4634e11", storeRuleService.fetchRule(1, 1).getRuleId());
    }

    @Test
    public void testRemoveRule() {
        storeRuleService.deleteRule(1, "6580746c73981656f4634e11");
    }
}
