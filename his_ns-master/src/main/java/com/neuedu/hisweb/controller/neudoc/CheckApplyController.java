package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.CheckApply;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import com.neuedu.hisweb.service.ICheckApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/checkapply")
public class CheckApplyController {

    @Autowired
    private ICheckApplyService iService;

    @GetMapping("/page")
    public JsonResult<Page<CheckApplyVo>> getCheckApplyPage(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "registId", required = false) Integer registId,
            @RequestParam(value = "recordType", required = false) Integer recordType) {

        Page<CheckApplyVo> page = Page.of(pn, count);
        iService.selectPage(page, registId, recordType);

        return new JsonResult<>(page);
    }

    @PostMapping("/add")
    public JsonResult<Object> addCheckApply(@RequestBody List<CheckApply> items) {
        boolean result = iService.addItems(items);
        if (result) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("新增失败");
        }
    }

    @PostMapping("/saveOrUpdateBatch")
    public JsonResult<Object> saveOrUpdateBatch(@RequestBody List<CheckApply> items) {
        boolean result = iService.saveOrUpdateBatch(items);
        if (result) {
            return new JsonResult<>(items);
        } else {
            return new JsonResult<>("操作失败");
        }
    }

    @PostMapping("/updateState")
    public JsonResult<Object> updateState(@RequestBody Map<String, Object> params) {
        List<Integer> ids = (List<Integer>) params.get("ids");
        Integer state = (Integer) params.get("state");
        boolean result = iService.updateState(ids, state);
        if (result) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("操作失败");
        }
    }

    @PostMapping("/del")
    public JsonResult<Object> delCheckApply(@RequestBody List<Integer> ids) {
        boolean result = iService.delete(ids);
        if (result) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("删除失败");
        }
    }
}
 