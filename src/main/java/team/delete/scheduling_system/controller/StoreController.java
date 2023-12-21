package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.service.StoreService;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {
    final StoreService storeService;

    /**
     * 查询所有门店接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping
    public Object fetchAllStore() {
        return AjaxResult.SUCCESS(storeService.fetchAllStore(StpUtil.getLoginIdAsInt()));
    }

    /**
     * 添加门店接口
     *
     * @param store 参数形式传入的门店对象
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping
    public Object addStore(@RequestBody Store store) {
        storeService.addStore(StpUtil.getLoginIdAsInt(), store);
        return AjaxResult.SUCCESS();
    }

    /**
     * 修改门店接口
     *
     * @param store 参数形式传入的门店对象
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PutMapping
    public Object updateStore(@RequestBody Store store) {
        storeService.updateStore(StpUtil.getLoginIdAsInt(), store);
        return AjaxResult.SUCCESS();
    }

    /**
     * 删除门店接口
     *
     * @param id 参数形式传入的门店id
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @DeleteMapping("/{id}")
    public Object deleteStore(@PathVariable Integer id) {
        storeService.deleteStore(StpUtil.getLoginIdAsInt(), id);
        return AjaxResult.SUCCESS();
    }

}
