package team.delete.scheduling_system.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.CustomerFlow;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.CustomerFlowMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.io.File;
import java.util.List;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Service
@Slf4j
@AllArgsConstructor
public class CustomerFlowService {
    private final CustomerFlowMapper customerFlowMapper;

    private final UserMapper userMapper;

    /**
     * 通过解析excel文件插入数据
     * @param userId 操作的用户对象id
     * @param file excel文件
     */
    public void insertByExcel(Integer userId, File file) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (user.getType() != User.Type.SUPER_ADMIN && user.getType() != User.Type.MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        EasyExcel.read(file, CustomerFlow.class, new ReadListener<CustomerFlow>() {
            /**
             * 单次缓存的数据量
             */
            public static final int BATCH_COUNT = 100;
            /**
             *临时存储
             */
            private List<CustomerFlow> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(CustomerFlow data, AnalysisContext context) {
                cachedDataList.add(data);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }

            /**
             * 加上存储数据库
             */
            private void saveData() {
                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
                cachedDataList.forEach(customerFlowMapper::insert);
                log.info("存储数据库成功！");
            }
        }).sheet().doRead();
        file.delete();
    }
}
