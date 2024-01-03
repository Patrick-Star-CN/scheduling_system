package team.delete.scheduling_system.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerFlow {
    /**
     * 客流量id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 门店id
     */
    @ExcelProperty("门店 ID")
    private Integer storeId;
    /**
     * 日期
     */
    @ExcelProperty("日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String date;
    /**
     * 开始时间
     */
    @ExcelProperty("开始时间")
    private String startTime;
    /**
     * 结束时间
     */
    @ExcelProperty("结束时间")
    private String endTime;
    /**
     * 客流量
     */
    @ExcelProperty("预测顾客流量")
    private Double flow;

}
