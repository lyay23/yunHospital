package com.neuedu.hisweb.controller.neusys;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantItem;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.service.IConstantItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-07-26
 */
@RestController
@RequestMapping("/constantitem")
public class ConstantItemController {
    @Autowired
    private IConstantItemService iConstantItemService;

    @GetMapping("/page")
    public JsonResult<Page> getConstantItemPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                            @RequestParam(value = "count", defaultValue = "10") Integer count,
                                            @RequestParam(value = "keyword",required = false)String keyword,
                                            @RequestParam(value = "ctype",required = false)String ctype){
        Page<ConstantItemVo> page=Page.of(pn,count);
        iConstantItemService.selectPage(page,keyword,ctype);
        return new JsonResult<Page>(page);
    }

    @GetMapping("/listByType")
    public JsonResult<List<ConstantItemVo>> getConstantItemList(@RequestParam(value = "keywords",required = false)String keywords){
        List<ConstantItemVo> list= iConstantItemService.selectByType(keywords);
        return new JsonResult<List<ConstantItemVo>>(list);
    }

    @PostMapping("/add")
    public JsonResult<ConstantItem>addConstantItem(@RequestBody ConstantItem constantItem){
        boolean rs= iConstantItemService.save(constantItem);
        if(rs)return new JsonResult<ConstantItem>(constantItem);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<ConstantItem> updateConstantItem(@RequestBody ConstantItem constantitem){
        boolean rs= iConstantItemService.updateById(constantitem);
        if(rs)return new JsonResult<ConstantItem>(constantitem);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<ConstantItem> delConstantItem(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iConstantItemService.removeById(id);
        if(rs){
            JsonResult<ConstantItem> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }

}

