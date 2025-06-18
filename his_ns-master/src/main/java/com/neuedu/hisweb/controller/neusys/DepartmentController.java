package com.neuedu.hisweb.controller.neusys;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Department;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.vo.DepartmentVo;
import com.neuedu.hisweb.service.IDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lynn
 * @since 2023-07-29
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {
    @Autowired
    private IDepartmentService iDepartmentService;

    @GetMapping("/page")
    public JsonResult<Page> getDepartmentPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                                @RequestParam(value = "count", defaultValue = "10") Integer count,
                                                @RequestParam(value = "keyword",required = false)String keyword,
                                                @RequestParam(value = "ctype",required = false)String ctype,
                                                @RequestParam(value = "dtype",required = false)String dtype){
        Page<DepartmentVo> page=Page.of(pn,count);
        iDepartmentService.selectPage(page,keyword,dtype,ctype);
        return new JsonResult<Page>(page);
    }


    @PostMapping("/add")
    public JsonResult<Department>addDepartment(@RequestBody Department department){
        boolean rs= iDepartmentService.save(department);
        if(rs)return new JsonResult<Department>(department);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<Department> updateDepartment(@RequestBody Department department){
        boolean rs= iDepartmentService.updateById(department);
        if(rs)return new JsonResult<Department>(department);
        else return new JsonResult<>("修改失败");
    }

    @PostMapping("/del")
    public JsonResult<Department> delDepartment(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iDepartmentService.removeById(id);
        if(rs){
            JsonResult<Department> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }
}

