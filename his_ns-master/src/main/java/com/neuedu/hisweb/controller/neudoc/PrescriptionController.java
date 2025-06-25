package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.Prescription;
import com.neuedu.hisweb.entity.vo.PrescriptionVo;
import com.neuedu.hisweb.service.IPrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
@RequestMapping("/prescription")
public class PrescriptionController {

    @Autowired
    private IPrescriptionService iPrescriptionService;

    @GetMapping("/page")
    public JsonResult<Page<PrescriptionVo>> getPrescriptionPage(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "registId") Integer registId) {

        Page<PrescriptionVo> page = new Page<>(pn, count);
        page = iPrescriptionService.selectPage(page, registId);

        return new JsonResult<>(page);
    }

    @PostMapping("/add")
    public JsonResult<Prescription> addPrescription(@RequestBody Prescription prescription) {
        //设置默认值
        prescription.setPrescriptionTime(LocalDateTime.now());
        prescription.setPrescriptionState(1);//1-暂存
        boolean success = iPrescriptionService.save(prescription);
        if (success) {
            return new JsonResult<>(prescription);
        }
        return new JsonResult<>("添加失败");
    }
    
    @PostMapping("/addByTemplate")
    public JsonResult<Object> addByTemplate(@RequestBody PrescriptionVo prescriptionVo) {
        Boolean success = iPrescriptionService.saveByTemplate(prescriptionVo);
        if (success) {
            return new JsonResult<>(true);
        }
        return new JsonResult<>("模板引用失败");
    }

    @PostMapping("/del")
    public JsonResult<Object> deletePrescription(@RequestBody List<Integer> ids) {
        boolean success = iPrescriptionService.removeByIds(ids);
        if (success) {
            return new JsonResult<>(true);
        }
        return new JsonResult<>("删除失败");
    }
    
    @PostMapping("/updateState")
    public JsonResult<Object> updatePrescriptionState(@RequestBody PrescriptionVo prescriptionVo) {
        boolean success = iPrescriptionService.updateState(prescriptionVo.getIds(), prescriptionVo.getPrescriptionState());
        if (success) {
            return new JsonResult<>(true);
        }
        return new JsonResult<>("状态更新失败");
    }
} 