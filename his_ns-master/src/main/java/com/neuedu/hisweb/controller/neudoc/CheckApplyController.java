package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.CheckApply;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.CheckApplyVo;
import com.neuedu.hisweb.entity.vo.CheckResultVo;
import com.neuedu.hisweb.service.ICheckApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/checkapply")
public class CheckApplyController {

    @Autowired
    private ICheckApplyService iService;

    @GetMapping("/page")
    public JsonResult<IPage<CheckApplyVo>> getCheckApplyPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                         @RequestParam(value = "count", defaultValue = "10") Integer count,
                                                         CheckApplyVo keywords){
        Page<CheckApplyVo> pageDomain=new Page<>(page,count);
        if(keywords==null)keywords=new CheckApplyVo();
        return JsonResult.success(iService.selectPage(pageDomain,keywords));
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

    @PostMapping("/execute")
    public JsonResult<Object> executeCheck(@RequestBody List<Integer> ids){
        boolean result = iService.executeCheck(ids);
        return result ? new JsonResult<>(true) : new JsonResult<>("执行失败");
    }

    @PostMapping("/cancel-execute")
    public JsonResult<Object> cancelExecuteCheck(@RequestBody List<Integer> ids){
        boolean result = iService.cancelExecuteCheck(ids);
        return result ? new JsonResult<>(true) : new JsonResult<>("取消失败");
    }

    @PostMapping("/save-result")
    public JsonResult<Boolean> saveResult(@RequestBody CheckResultVo resultVo) {
        boolean result = iService.saveResult(resultVo);
        return JsonResult.success(result);
    }

    @GetMapping("/getCheckApply")
    public JsonResult<Map<String, Object>> getCheckApply(@RequestParam("registId") Integer registId) {
        Map<String, Object> result = iService.getCheckApplyDetails(registId);
        if (result != null && !result.isEmpty()) {
            return new JsonResult<>(result);
        } else {
            return new JsonResult<>(new HashMap<>());
        }
    }
}
