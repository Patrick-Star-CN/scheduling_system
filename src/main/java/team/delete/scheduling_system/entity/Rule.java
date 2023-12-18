package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;
import java.util.Map;

/**
 * @author Devin100086
 * @version 1.0
 */
@Data
@Builder
@ToString
@Document(collection = "store_rules")
public class Rule {
    /**
     * 排班规则id
     */
    @Id
    @MongoId
    @Field("rule_id")
    String ruleId;
    /**
     * 开店规则
     */
    @Field("open_store")
    RuleDetail openStore;
    /**
     * 闭店规则
     */
    @Field("close_store")
    RuleDetail closeStore;
    /**
     * 客流规则
     */
    @Field("passenger")
    RuleDetail passenger;
    /**
     * 门店id
     */
    @Field("store_id")
    Integer storeId;
}

