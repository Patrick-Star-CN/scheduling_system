package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.service.ChangeShiftService;
import team.delete.scheduling_system.service.CustomerFlowService;
import team.delete.scheduling_system.service.ScheduleService;
import team.delete.scheduling_system.service.ShiftService;
import team.delete.scheduling_system.util.FileUtil;

import java.io.IOException;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Validated
@RestController
@CrossOrigin
@RequestMapping("/api/shift")
@RequiredArgsConstructor
public class ShiftController {
    private final ShiftService shiftService;
    private final ChangeShiftService changeShiftService;
    private final ScheduleService scheduleService;
    private final CustomerFlowService customerFlowService;

    /**
     * 查询班次框架接口
     *
     * @param id 门店id
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping("/{id}")
    public Object fetchShift(@PathVariable Integer id) {
        return AjaxResult.SUCCESS(shiftService.fetchShift(StpUtil.getLoginIdAsInt(), id));
    }

    /**
     * 查询班次安排接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping("/schedule/{id}")
    public Object fetchSchedule(@PathVariable Integer id) {
        return AjaxResult.SUCCESS(scheduleService.fetchScheduleList(StpUtil.getLoginIdAsInt(), id));
    }

    /**
     * 生成排班接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping("/{id}")
    public Object initSchedule(@PathVariable Integer id) {
        shiftService.initShift(StpUtil.getLoginIdAsInt(), id);
        scheduleService.initSchedule(StpUtil.getLoginIdAsInt(), id);
        return AjaxResult.SUCCESS();
    }

    /**
     * 导入客流数据接口
     *
     * @param file excel 文件
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @PostMapping("/customer-flow")
    public Object importCustomerFlow(@RequestParam("file") MultipartFile file) throws IOException {
        customerFlowService.insertByExcel(StpUtil.getLoginIdAsInt(), FileUtil.convertToFile(file));
        return AjaxResult.SUCCESS();
    }
    @ResponseBody
    @PostMapping("/change-shift")
    public  Object changeShift(@RequestParam(value = "store_id") Integer storeId,
                               @RequestParam(value = "user_id1") Integer userId1, @RequestParam(value = "user_id2") Integer userId2,
                               @RequestParam(value = "shift_id1") Integer shiftId1, @RequestParam(value = "shift_id2") Integer shiftId2,
                               @RequestParam(value = "week_id1") Integer weekId1, @RequestParam(value = "week_id2") Integer weekId2) {
        changeShiftService.changeShift(StpUtil.getLoginIdAsInt(), storeId, userId1, userId2, shiftId1, shiftId2, weekId1, weekId2);
        return AjaxResult.SUCCESS();
    }
    /**
     * 查询客流数据接口
     *
     * @return json数据，包含状态码和状态信息
     */
    @ResponseBody
    @GetMapping("/customer-flow")
    public Object fetchCustomerFlow() {
        return AjaxResult.SUCCESS(customerFlowService.fetchAllCustomerFlow(StpUtil.getLoginIdAsInt()));
    }
}
