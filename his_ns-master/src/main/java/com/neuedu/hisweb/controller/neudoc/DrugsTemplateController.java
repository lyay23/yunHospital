package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.DrugsTemplate;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.DrugsTemplateVo;
import com.neuedu.hisweb.service.IDrugsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2024-07-27
 */
@RestController
@RequestMapping("/drugstemplate")
public class DrugsTemplateController {

    @Autowired
    private IDrugsTemplateService iDrugsTemplateService;

    @GetMapping("/page")
    public JsonResult<Page<DrugsTemplate>> getPage(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "userId", required = false) Integer userId) {
        Page<DrugsTemplate> page = iDrugsTemplateService.selectPage(new Page<>(pn, count), keyword, scope, userId);
        return new JsonResult<>(page);
    }

    @GetMapping("/details")
    public JsonResult<List<DrugsTemplateVo>> getDetails(
            @RequestParam("templateId") String templateId) {
        List<DrugsTemplateVo> details = iDrugsTemplateService.selectDetail(templateId);
        return new JsonResult<>(details);
    }
} 