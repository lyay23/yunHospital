package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.PrescriptionDetailed;
import com.neuedu.hisweb.entity.vo.PrescriptionDetailedVo;
import com.neuedu.hisweb.service.IPrescriptionDetailedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2024-07-26
 */
@RestController
@RequestMapping("/prescriptiondetailed")
public class PrescriptionDetailedController {
    @Autowired
    private IPrescriptionDetailedService iPrescriptionDetailedService;

    @GetMapping("/page")
    public JsonResult<Page<PrescriptionDetailedVo>> getPage(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "prescriptionId") Integer prescriptionId) {

        Page<PrescriptionDetailedVo> page = new Page<>(pn, count);
        page = iPrescriptionDetailedService.selectPage(page, prescriptionId);
        return new JsonResult<>(page);
    }

    @PostMapping("/add")
    public JsonResult<PrescriptionDetailed> add(@RequestBody PrescriptionDetailed prescriptionDetailed) {
        boolean success = iPrescriptionDetailedService.save(prescriptionDetailed);
        if (success) {
            return new JsonResult<>(prescriptionDetailed);
        }
        return new JsonResult<>("添加失败");
    }

    @PostMapping("/del")
    public JsonResult<Boolean> delete(@RequestBody List<Integer> ids) {
        boolean success = iPrescriptionDetailedService.removeByIds(ids);
        if (success) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("删除失败");
        }
    }

    @PostMapping("/update")
    public JsonResult<Boolean> update(@RequestBody PrescriptionDetailed prescriptionDetailed) {
        boolean success = iPrescriptionDetailedService.updateById(prescriptionDetailed);
        if (success) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("修改失败");
        }
    }
} 