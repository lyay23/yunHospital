package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.CheckTemplate;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.CheckTemplateItemVo;
import com.neuedu.hisweb.service.ICheckTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checktemplate")
public class CheckTemplateController {

    @Autowired
    private ICheckTemplateService iService;

    @GetMapping("/page")
    public JsonResult<Page<CheckTemplate>> getCheckTemplatePage(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "recordType", required = false) Integer recordType,
            @RequestParam(value = "doctorId", required = false) Integer doctorId) {

        Page<CheckTemplate> page = Page.of(pn, count);
        QueryWrapper<CheckTemplate> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("Name", keyword);
        }
        if (recordType != null) {
            wrapper.eq("RecordType", recordType);
        }

        wrapper.eq("DelMark", 1);

        // 加载全院模板(scope=1)和个人模板(scope=3)
        if (doctorId != null) {
            wrapper.and(wq -> wq.in("Scope", "1", "2")
                    .or(iq -> iq.eq("Scope", "3").eq("UserID", doctorId)));
        } else if (scope != null && !scope.isEmpty()) {
            wrapper.in("Scope", scope);
        }
        
        wrapper.orderByDesc("CreationTime");

        iService.page(page, wrapper);

        return new JsonResult<>(page);
    }

    @PostMapping("/add")
    public JsonResult<Object> addCheckTemplate(@RequestBody CheckTemplateItemVo templateVo) {
        boolean result = iService.saveTemplate(templateVo);
        if (result) {
            return new JsonResult<>(true);
        } else {
            return new JsonResult<>("新增失败");
        }
    }

    @PostMapping("/del")
    public JsonResult<Object> delCheckTemplate(@RequestBody CheckTemplate template) {
        template.setDelMark(0);
        boolean result = iService.updateById(template);
        if(result){
            return new JsonResult<>(true);
        }
        return new JsonResult<>("删除失败");
    }
} 