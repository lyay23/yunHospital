package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.Rule;
import com.neuedu.hisweb.entity.vo.RuleVo;
import com.neuedu.hisweb.service.IRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

    @Autowired
    private IRuleService iService;

    @GetMapping("/page")
    public JsonResult<Page> getRulePage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword,
                                            @RequestParam(value = "deptId",required = false)String deptId){
        Page<RuleVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword,deptId);
        return new JsonResult<Page>(page);
    }

    @PostMapping("/add")
    public JsonResult<Rule> addRule(@RequestBody Rule rule){
        boolean rs= iService.save(rule);
        if(rs)return new JsonResult<Rule>(rule);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Rule> updateRule(@RequestBody Rule rule){
        boolean rs= iService.updateById(rule);
        if(rs)return new JsonResult<Rule>(rule);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Rule> delRule(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Rule> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

}

