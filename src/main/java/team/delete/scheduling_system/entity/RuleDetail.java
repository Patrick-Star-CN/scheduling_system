package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author patrick_star
 * @version 1.0
 */
@Data
@Builder
@ToString
@Accessors(chain = true)
public class RuleDetail {
    /**
     * 开店前或关店后的若干分钟
     */
    private Integer time;
    /**
     * 至少要的人数
     */
    private Integer count;
    /**
     * 每 formula 个面积需要一个员工
     */
    private Double formula;
}
