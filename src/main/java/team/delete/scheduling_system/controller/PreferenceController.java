package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.dto.PreferenceDetailDto;
import team.delete.scheduling_system.entity.Preference;
import team.delete.scheduling_system.entity.PreferenceDetail;
import team.delete.scheduling_system.service.PreferenceService;

import java.util.Map;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/preference")
@RequiredArgsConstructor
public class PreferenceController {
    final PreferenceService preferenceService;

    /**
     * 添加偏好接口
     *
     * @param preferenceDetail 参数形式传入的偏好对象
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping
    public Object addPreference(@RequestBody PreferenceDetail preferenceDetail) {
        preferenceService.addPreference(StpUtil.getLoginIdAsInt(), preferenceDetail);
        return AjaxResult.SUCCESS();
    }

    /**
     * 查询偏好接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping
    public Object findPreference() {
        Preference preference = preferenceService.findPreferenceByUserId(StpUtil.getLoginIdAsInt());
        return AjaxResult.SUCCESS(preference);
    }

    /**
     * 修改偏好接口
     *
     * @param preferenceDetailDto 参数形式传入的偏好对象列表
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PutMapping
    public Object updatePreference(@RequestBody PreferenceDetailDto preferenceDetailDto) {
        preferenceService.updatePreference(StpUtil.getLoginIdAsInt(),
                preferenceDetailDto.getOldPreferenceDetailId(),
                preferenceDetailDto.getNewPreferenceDetail());
        return AjaxResult.SUCCESS();
    }

    /**
     * 删除偏好接口
     *
     * @param id 参数形式传入的偏好对象编号
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @DeleteMapping("/{id}")
    public Object deletePreference(@PathVariable Integer id) {
        preferenceService.deletePreferenceByUserId(StpUtil.getLoginIdAsInt(), id);
        return AjaxResult.SUCCESS();
    }

}
