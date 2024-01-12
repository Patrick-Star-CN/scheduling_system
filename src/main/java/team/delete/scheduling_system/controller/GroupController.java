package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.entity.Group;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.service.GroupService;

/**
 * @author Patrick_Star
 * @version 1.4
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {
    final GroupService groupService;

    /**
     * 添加小组接口（副经理）
     *
     * @param managerId 参数形式传入的小组负责人id
     * @param name 参数形式传入的组别名称
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping("/{manager_id}/{name}")
    public Object addGroupVice(@PathVariable("manager_id") Integer managerId, @PathVariable("name") String name) {
        groupService.addGroupVice(StpUtil.getLoginIdAsInt(), managerId, name);
        return AjaxResult.SUCCESS();
    }

    /**
     * 添加组别接口（经理）
     *
     * @param managerId 参数形式传入的小组负责人id
     * @param type 参数形式传入的组别类型
     * @param name 参数形式传入的组别名称
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping("/{manager_id}/{name}/{type}")
    public Object addGroup(@PathVariable("manager_id") Integer managerId, @PathVariable("name") String name, @PathVariable("type") String type) {
        groupService.addGroup(StpUtil.getLoginIdAsInt(), managerId, Enum.valueOf(User.Type.class, type), name);
        return AjaxResult.SUCCESS();
    }
    /**
     * 查询所有组别接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping
    public Object fetchAllGroup() {
        return AjaxResult.SUCCESS(groupService.fetchAllGroup(StpUtil.getLoginIdAsInt()));
    }

    /**
     * 修改组别接口
     *
     * @param group 参数形式传入的工种对象
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PutMapping
    public Object updateGroup(@RequestBody Group group) {
        groupService.updateGroup(StpUtil.getLoginIdAsInt(), group);
        return AjaxResult.SUCCESS();
    }

    /**
     * 删除组别接口
     *
     * @param id 参数形式传入的工种id
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @DeleteMapping("/{id}")
    public Object deleteGroup(@PathVariable Integer id) {
        groupService.deleteGroup(StpUtil.getLoginIdAsInt(), id);
        return AjaxResult.SUCCESS();
    }


    /**
     * 查询门店某一工种小组列表接口
     *
     * @param storeId 参数形式传入的店铺id
     * @param type 参数形式传入的工种
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping("/list")
    public Object findGroupListByStoreIdAndType(@RequestParam(value = "store_id") int storeId, @RequestParam(value = "type") User.Type type) {
        return AjaxResult.SUCCESS(groupService.fetchGroupListByTypeAndStoreId(StpUtil.getLoginIdAsInt(), type, storeId));
    }

    /**
     * 查询某门店某工种的小组列表接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping("/employee")
    public Object findGroupListByUserId() {
        return AjaxResult.SUCCESS(groupService.fetchGroupListByUserId(StpUtil.getLoginIdAsInt()));
    }
}