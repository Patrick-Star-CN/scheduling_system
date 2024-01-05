package team.delete.scheduling_system.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.delete.scheduling_system.dto.AjaxResult;
import team.delete.scheduling_system.service.CustomerFlowService;
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
        return AjaxResult.SUCCESS(shiftService.fetchScheduleList(StpUtil.getLoginIdAsInt(), id));
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
        shiftService.initSchedule(StpUtil.getLoginIdAsInt(), id);
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
}
