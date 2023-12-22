package team.delete.scheduling_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.StoreMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.List;

/**
 * @author Devin100086
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class StoreService {
    final StoreMapper storeMapper;
    final UserMapper userMapper;

    /**
     * 判断权限
     *
     * @param userId 需要判断权限的用户对象id
     */
    public void judgePermission(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.SUPER_ADMIN) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询所有门店
     *
     * @param userId 操作的用户
     * @return 门店信息列表
     */
    public List<Store> fetchAllStore(Integer userId) {
        judgePermission(userId);
        return storeMapper.selectList(null);
    }

    /**
     * 增加门店信息
     *
     * @param userId  操作的用户
     * @param store 新增的门店对象
     */
    public void addStore(Integer userId, Store store) {
        if (store == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgePermission(userId);
        storeMapper.insert(store);
    }

    /**
     * 删除门店信息
     *
     * @param userId    操作的用户
     * @param storeId 新增的门店对象id
     */
    public void deleteStore(Integer userId, Integer storeId) {
        if (storeId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgePermission(userId);
        storeMapper.deleteById(storeId);
    }

    /**
     * 更新商店信息
     *
     * @param userId  操作的用户
     * @param store 新增的门店对象
     */
    public void updateStore(Integer userId, Store store) {
        if (store == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgePermission(userId);
        storeMapper.updateById(store);
    }
}
