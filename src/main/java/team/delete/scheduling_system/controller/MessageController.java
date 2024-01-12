package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.service.MessageService;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {
    final MessageService messageService;

    @ResponseBody
    @GetMapping
    public Object fetchAllMessages() {
        return AjaxResult.SUCCESS(messageService.getMessage(StpUtil.getLoginIdAsInt()));
    }

    @ResponseBody
    @PostMapping
    public Object addMessage(@RequestParam(value = "user_id") Integer userId, @RequestParam String content) {
        messageService.sendMessage(userId, content);
        return AjaxResult.SUCCESS();
    }
}
