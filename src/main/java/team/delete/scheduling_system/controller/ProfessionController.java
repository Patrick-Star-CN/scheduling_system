package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.service.ProfessionService;

/**
 * @author cookie1551
 * @version 1.0
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/profession")
@RequiredArgsConstructor
public class ProfessionController {
    final ProfessionService professionService;

    /**
     * 查询所有工种接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping
    public Object fetchAllProfession() {
        return AjaxResult.SUCCESS(professionService.fetchAllProfession(StpUtil.getLoginIdAsInt()));
    }

    /**
     * 添加工种接口
     *
     * @param managerId 参数形式传入的工种负责人id
     * @param professionType 参数形式传入的工种类型名称
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping
    public Object addProfession(@RequestBody Integer managerId, @RequestParam User.Type professionType) {
        professionService.addProfession(StpUtil.getLoginIdAsInt(),managerId, professionType);
        return AjaxResult.SUCCESS();
    }

    /**
     * 修改工种接口
     *
     * @param profession 参数形式传入的工种对象
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PutMapping
    public Object updateProfession(@RequestBody Profession profession) {
        professionService.updateProfession(StpUtil.getLoginIdAsInt(), profession);
        return AjaxResult.SUCCESS();
    }

    /**
     * 删除工种接口
     *
     * @param id 参数形式传入的工种id
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @DeleteMapping("/{id}")
    public Object deleteProfession(@PathVariable Integer id) {
        professionService.deleteProfession(StpUtil.getLoginIdAsInt(), id);
        return AjaxResult.SUCCESS();
    }
}
