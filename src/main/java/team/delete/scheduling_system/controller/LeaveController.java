package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.dto.LeaveRequestDto;
import team.delete.scheduling_system.dto.UserInsertDto;
import team.delete.scheduling_system.service.LeaveRecordService;

import java.time.LocalDate;
import java.util.Map;

/**
 * 请假记录相关接口
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/leave")
@RequiredArgsConstructor
public class LeaveController {

    final LeaveRecordService leaveRecordService;

    /**
     * 查询自己的所有请假记录
     */
    @ResponseBody
    @GetMapping("/all")
    public AjaxResult fetchAllLeaveRecord() {
        Integer userId = StpUtil.getLoginIdAsInt();
        return AjaxResult.SUCCESS(leaveRecordService.fetchAllLeaveRecord(userId));
    }

    /**
     * 查询自己的某个时间区间请假记录
     */
    @GetMapping("/range")
    public AjaxResult fetchAllLeaveRecordByRange(@RequestParam LocalDate startTime,
                                                 @RequestParam LocalDate endTime) {
        Integer userId = StpUtil.getLoginIdAsInt();
        return AjaxResult.SUCCESS(leaveRecordService.fetchAllLeaveRecordByRange(userId, startTime, endTime));
    }

    /**
     * 根据记录id删除未审核的记录
     */
    @ResponseBody
    @DeleteMapping("/{recordId}")
    public AjaxResult deleteLeaveRecordByRecordId(@PathVariable Integer recordId) {
        Integer userId = StpUtil.getLoginIdAsInt();
        leaveRecordService.deleteLeaveRecordByRecordId(userId, recordId);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addUser(@RequestBody LeaveRequestDto leaveRequestDto) {
        Integer userId = StpUtil.getLoginIdAsInt();
        LocalDate leaveTime = leaveRequestDto.getLeaveTime();
        Integer scheduleShift = leaveRequestDto.getScheduleShift();
        leaveRecordService.addLeaveRecord(userId, leaveTime, scheduleShift);
        return AjaxResult.SUCCESS();
    }

    @ResponseBody
    @GetMapping("/allreview")
    public AjaxResult fetchAllReviewLeaveRecord() {
        Integer userId = StpUtil.getLoginIdAsInt();
        return AjaxResult.SUCCESS(leaveRecordService.fetchAllReviewLeaveRecord(userId));
    }

    @ResponseBody
    @PostMapping("/review")
    public Object reviewLeaveRecord(@RequestBody Map<Integer, Integer> map) {
        Integer userId = StpUtil.getLoginIdAsInt();
        Integer recordId = map.get("recordId");
        boolean result = (map.get("status")).equals(1);
        leaveRecordService.reviewLeaveRecord(userId, recordId, result);
        return AjaxResult.SUCCESS();
    }
}