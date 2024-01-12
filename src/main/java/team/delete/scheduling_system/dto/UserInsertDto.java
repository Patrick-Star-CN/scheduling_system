package team.delete.scheduling_system.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import team.delete.scheduling_system.constant.CustomTypeStringConverter;
import team.delete.scheduling_system.entity.User;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@NoArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInsertDto {
    /**
     * 用户名
     */
    @ExcelProperty("name")
    String name;
    /**
     * 门店id
     */
    @ExcelProperty("store_id")
    Integer storeId;
    /**
     * 小组id
     */
    @ExcelProperty("group_id")
    Integer groupId;
    /**
     * 雇员职位
     */
    @ExcelProperty(converter = CustomTypeStringConverter.class, value = "type")
    User.Type type;
}