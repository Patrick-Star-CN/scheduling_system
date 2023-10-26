package team.delete.scheduling_system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.service.UserService;

import java.util.Map;

/**
 * 用户相关接口
 *
 * @author Patrick_Star
 * @version 1.0
 */
@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    /**
     * 登录接口
     *
     * @param map 参数形式传入的用户名和密码
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping("/login")
    public Object login(@RequestBody Map<String, String> map) {
        userService.login(map.get("username"), map.get("password"));
        return AjaxResult.SUCCESS();
    }

}
