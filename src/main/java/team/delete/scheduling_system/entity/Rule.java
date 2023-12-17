package team.delete.scheduling_system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author Devin100086
 * @version 1.0
 */
@Data
@Builder
public class Rule {
    /**
     * 排班规则id
     */
    @TableId(type = IdType.AUTO)
    Integer ruleId;
    /**
     * 开店规则
     */
    Map<String,String> openStore;
    /**
     * 闭店规则
     */
    Map<String,String> closeStore;
    /**
     * 客流规则
     */
    Map<String,String> passenger;
    /**
     * 门店
     */
    String store;
    /**
     * 排班规则类型
     */
    public enum Type{
        /**
         * 开店规则
         */
        OPEN,
        /**
         * 闭店规则
         */
        CLOSE,
        /**
         * 客流规则
         */
        PASSENGER
    }
}
