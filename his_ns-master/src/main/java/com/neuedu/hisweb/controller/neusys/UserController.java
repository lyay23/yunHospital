package com.neuedu.hisweb.controller.neusys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.JsonResult;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.entity.vo.UserVo;
import com.neuedu.hisweb.service.IUserService;
import com.neuedu.hisweb.utils.JwtUtils;
import com.neuedu.hisweb.utils.MD5Util;
import com.neuedu.hisweb.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户控制器类，处理用户相关的接口请求
 * 提供用户登录、登出、分页查询、添加、修改、删除等功能
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    // 注入JWT工具类实例，用于生成JWT令牌
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户登录接口
     * @param request HTTP请求对象，用于存储会话信息
     * @param user 包含用户名和密码的User对象
     * @return JsonResult对象，包含登录结果和用户信息
     */
    @PostMapping("/login")
    public JsonResult<User> login(HttpServletRequest request, @RequestBody User user){
        String uname = user.getUserName();
        String pwd = user.getPassword();

        // 对传入的密码进行MD5加密
        String md5Pwd = MD5Util.getMD5(pwd);

        // 构建查询条件：用户名、加密后的密码匹配且未删除
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, uname)
                .eq(User::getPassword, md5Pwd)
                .eq(User::getDelMark, 1);

        // 调用服务层查询用户
        user = iUserService.getOne(wrapper);
        JsonResult<User> jsonResult;

        if (user == null) {
            // 登录失败
            jsonResult = new JsonResult<>("用户名或密码不正确！");
        } else {
            // 登录成功
            UserUtils.setLoginUser(user);
            request.getSession().setAttribute("user", user);
            String token = jwtUtils.createToken(user);
            // 将token和用户信息封装到JsonResult中返回
            jsonResult = new JsonResult<>(user, token);
        }
        return jsonResult;
    }

    /**
     * 用户登出接口
     * @param request HTTP请求对象，用于清除会话信息
     * @return JsonResult对象，包含登出结果
     */
    @PostMapping("/logout")
    public JsonResult<User> logout(HttpServletRequest request){
        // 移除会话中的用户信息
        request.getSession().removeAttribute("user");
        // 使会话失效
        request.getSession().invalidate();
        // 返回登出结果
        JsonResult<User> jsonResult = new JsonResult<>(null, "");
        return jsonResult;
    }

    /**
     * 用户分页查询接口
     * @param pn 页码，默认第1页
     * @param count 每页记录数，默认10条
     * @param keyword 搜索关键词
     * @param userType 用户类型
     * @param dept 部门
     * @param docType 文档类型
     * @return JsonResult对象，包含分页数据
     */
    @GetMapping("/page")
    public JsonResult<Page> getUserPage(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                                        @RequestParam(value = "count", defaultValue = "10") Integer count,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "userType", required = false) String userType,
                                        @RequestParam(value = "dept", required = false) String dept,
                                        @RequestParam(value = "docType", required = false) String docType) {
        // 创建分页对象
        Page<UserVo> page = Page.of(pn, count);
        // 调用服务层进行分页查询
        iUserService.selectPage(page, keyword, userType, dept, docType);
        // 返回分页结果
        return new JsonResult<Page>(page);
    }

    /**
     * 添加用户接口
     * @param user 待添加的用户对象
     * @return JsonResult对象，包含添加结果
     */
    @PostMapping("/add")
    public JsonResult<User> addUser(@RequestBody User user){
        // 设置默认密码为"123456"的MD5哈希值
        user.setPassword(MD5Util.getMD5("123456"));
        // 调用服务层保存用户
        boolean rs = iUserService.save(user);
        if (rs) {
            // 添加成功，返回用户信息
            return new JsonResult<User>(user);
        } else {
            // 添加失败，返回错误信息
            return new JsonResult<>("添加失败");
        }
    }

    /**
     * 修改用户接口
     * @param user 待修改的用户对象
     * @return JsonResult对象，包含修改结果
     */
    @PostMapping("/update")
    public JsonResult<User> updateUser(@RequestBody User user){
        // 如果请求中包含了密码，则对其进行加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(MD5Util.getMD5(user.getPassword()));
        } else {
            // 防止密码被意外清空
            user.setPassword(null);
        }
        // 调用服务层更新用户
        boolean rs = iUserService.updateById(user);
        if (rs) {
            // 修改成功，返回用户信息
            return new JsonResult<User>(user);
        } else {
            // 修改失败，返回错误信息
            return new JsonResult<>("修改失败");
        }
    }

    /**
     * 修改密码接口
     * @param uid 用户ID
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @return JsonResult对象，包含修改结果
     */
    @PostMapping("/changePwd")
    public JsonResult<User> updateChangePwd(@RequestParam(value = "uid", required = true) Integer uid,
                                            @RequestParam(value = "oldPwd", required = true) String oldPwd,
                                            @RequestParam(value = "newPwd", required = true) String newPwd) {
        // 对密码进行MD5加密
        String md5OldPwd = MD5Util.getMD5(oldPwd);
        String md5NewPwd = MD5Util.getMD5(newPwd);

        // 调用服务层修改密码
        boolean rs = iUserService.updatePwd(uid, md5OldPwd, md5NewPwd);
        if (rs) {
            // 修改成功
            JsonResult<User> jsonResult = new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        } else {
            // 修改失败，返回错误信息
            return new JsonResult<>("修改失败");
        }
    }

    /**
     * 删除用户接口
     * @param id 用户ID
     * @return JsonResult对象，包含删除结果
     */
    @PostMapping("/del")
    public JsonResult<User> delUser(@RequestParam(value = "id", required = true) Integer id) {
        // 调用服务层删除用户
        boolean rs = iUserService.removeById(id);
        if (rs) {
            // 删除成功
            JsonResult<User> jsonResult = new JsonResult<>();
            jsonResult.setResult(true);
            return jsonResult;
        } else {
            // 删除失败，返回错误信息
            return new JsonResult<>("删除失败");
        }
    }
}