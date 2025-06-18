package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantType;
import com.neuedu.hisweb.entity.Disease;
import com.neuedu.hisweb.entity.Expenseclass;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.DiseaseVo;
import com.neuedu.hisweb.service.IDiseaseService;
import com.neuedu.hisweb.service.IExpenseclassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-08-01
 */
@RestController
@RequestMapping("/expenseclass")
public class ExpenseclassController {
    @Autowired
    private IExpenseclassService iService;

    @GetMapping("/page")
    public JsonResult<Page> getExpenseClassPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword){
        Page<Expenseclass> page=Page.of(pn,count);
        if(keyword!=null){
            LambdaQueryWrapper<Expenseclass> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.like(Expenseclass::getExpCode,keyword)
                    .or().like(Expenseclass::getExpName,keyword);
            iService.page(page,queryWrapper);
        }else
            iService.page(page);

        return new JsonResult<Page>(page);
    }


    @PostMapping("/add")
    public JsonResult<Expenseclass> addExpenseClass(@RequestBody Expenseclass expenseclass){
        boolean rs= iService.save(expenseclass);
        if(rs)return new JsonResult<Expenseclass>(expenseclass);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Expenseclass> updateExpenseClass(@RequestBody Expenseclass expenseclass){
        boolean rs= iService.updateById(expenseclass);
        if(rs)return new JsonResult<Expenseclass>(expenseclass);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Expenseclass> delExpenseClass(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<Expenseclass> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }
}

