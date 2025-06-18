package com.neuedu.hisweb.controller.neureg;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.MedicalCard;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.entity.vo.MedicalCardVo;
import com.neuedu.hisweb.entity.vo.UserVo;
import com.neuedu.hisweb.service.IMedicalCardService;
import com.neuedu.hisweb.service.IUserService;
import com.neuedu.hisweb.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *  医疗卡
 * @author lynn
 * @since 2023-08-09
 */
@RestController
@RequestMapping("/medicalcard")
public class MedicalCardController {
    @Autowired
    private IMedicalCardService iService;


    @GetMapping("/page")
    public JsonResult<Page> getUserPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                        @RequestParam(value = "count", defaultValue = "10") Integer count,
                                        @RequestParam(value = "keyword",required = false)String keyword){
        Page<MedicalCardVo> page=Page.of(pn,count);
        iService.selectPage(page,keyword);
        return new JsonResult<Page>(page);
    }

    @GetMapping("/list/{id}")
    public JsonResult<Object> list(@PathVariable("id") String id){
        LambdaQueryWrapper<MedicalCard> queryWrapper
                = new LambdaQueryWrapper<MedicalCard>().eq(MedicalCard::getCustomerId, id);
        return new JsonResult<Object>(iService.list(queryWrapper));
    }

    @GetMapping("/cnt/{id}")
    public JsonResult<Object> cnt(@PathVariable("id") String id){
        LambdaQueryWrapper<MedicalCard> queryWrapper
                = new LambdaQueryWrapper<MedicalCard>().eq(MedicalCard::getCustomerId, id);
        return new JsonResult<Object>(iService.count(queryWrapper));
    }

    @GetMapping("/info/{id}")
    public JsonResult<Object> info(@PathVariable("id") String id){
        return new JsonResult<Object>(iService.getById(id));
    }


    @PostMapping("/add")
    public JsonResult<MedicalCard>addUser(@RequestBody MedicalCard card){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        card.setCreatedate(sdf.format(new Date()));
        card.setCardNo(String.valueOf(iService.getMaxCardNo()+1));
        card.setDelMark(1);
        boolean rs= iService.save(card);
        if(rs)return new JsonResult<MedicalCard>(card);
        else return new JsonResult<>("添加失败");
    }

    @PostMapping("/update")
    public JsonResult<MedicalCard> updateUser(@RequestBody MedicalCard card){
        boolean rs= iService.updateById(card);
        if(rs)return new JsonResult<MedicalCard>(card);
        else return new JsonResult<>("修改失败");
    }


    @PostMapping("/del")
    public JsonResult<MedicalCard> delUser(@RequestParam(value = "id",required = true) Integer id){
        boolean rs= iService.removeById(id);
        if(rs){
            JsonResult<MedicalCard> jsonResult= new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        }
        else return new JsonResult<>("删除失败");
    }


}

