package com.neuedu.hisweb.controller.neudoc;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Drugs;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.service.IDrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drugs")
public class DrugsController {

    @Autowired
    private IDrugsService iDrugsService;

    @GetMapping("/page")
    public JsonResult<Page<Drugs>> getPage(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            @RequestParam(value = "count", defaultValue = "10") Integer count,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Page<Drugs> page = iDrugsService.selectPage(new Page<>(pn, count), keyword);
        return new JsonResult<>(page);
    }
} 