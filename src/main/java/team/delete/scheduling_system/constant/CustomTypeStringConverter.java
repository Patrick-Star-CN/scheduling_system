package team.delete.scheduling_system.constant;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import team.delete.scheduling_system.entity.User;

/**
 * @author Patrick_Star
 * @version 1.0
 */
public class CustomTypeStringConverter implements Converter<User.Type> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return User.Type.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 这里读的时候会调用
     *
     * @param context
     * @return
     */
    @Override
    public User.Type convertToJavaData(ReadConverterContext<?> context) {
        return User.Type.valueOf(context.getReadCellData().getStringValue());
    }

    /**
     * 这里是写的时候会调用 不用管
     *
     * @return
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<User.Type> context) {
        return new WriteCellData<>(String.valueOf(context.getValue()));
    }

}