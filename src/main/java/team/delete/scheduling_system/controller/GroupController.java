package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.entity.PreferenceDetail;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.service.GroupService;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {
    final GroupService groupService;

    /**
     * 添加小组接口
     *
     * @param managerId 参数形式传入的小组负责人id
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping
    public Object addPreference(@RequestParam Integer managerId, @RequestParam String groupName) {
        groupService.addGroup(StpUtil.getLoginIdAsInt(), managerId, groupName);
        return AjaxResult.SUCCESS();
    }

    /**
     * 查询店铺某一工种小组列表接口
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

}
