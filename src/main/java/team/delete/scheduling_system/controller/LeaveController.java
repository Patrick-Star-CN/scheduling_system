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
 *
 * @author YYHelen
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
    public Object fetchAllLeaveRecord() {
        return AjaxResult.SUCCESS(leaveRecordService.fetchAllLeaveRecord(StpUtil.getLoginIdAsInt()));
    }

    /**
     * 查询自己的某个时间区间请假记录
     */
    @GetMapping("/range")
    public Object fetchAllLeaveRecordByRange(@RequestParam LocalDate startTime,
                                                 @RequestParam LocalDate endTime) {
        return AjaxResult.SUCCESS(leaveRecordService.fetchAllLeaveRecordByRange(StpUtil.getLoginIdAsInt(), startTime, endTime));
    }

    /**
     * 根据记录id删除未审核的记录
     */
    @ResponseBody
    @DeleteMapping("/{recordId}")
    public Object deleteLeaveRecordByRecordId(@PathVariable Integer recordId) {
        leaveRecordService.deleteLeaveRecordByRecordId(StpUtil.getLoginIdAsInt(), recordId);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/add")
    @ResponseBody
    public Object addUser(@RequestBody LeaveRequestDto leaveRequestDto) {
        leaveRecordService.addLeaveRecord(StpUtil.getLoginIdAsInt(), leaveRequestDto.getLeaveTime(), leaveRequestDto.getScheduleShift());
        return AjaxResult.SUCCESS();
    }

    @ResponseBody
    @GetMapping("/all_review")
    public Object fetchAllReviewLeaveRecord() {
        return AjaxResult.SUCCESS(leaveRecordService.fetchAllReviewLeaveRecord(StpUtil.getLoginIdAsInt()));
    }

    @ResponseBody
    @PostMapping("/review")
    public Object reviewLeaveRecord(@RequestBody Map<String, Integer> map) {
        leaveRecordService.reviewLeaveRecord(StpUtil.getLoginIdAsInt(), map.get("record_id"), (map.get("status")).equals(1));
        return AjaxResult.SUCCESS();
    }
}