package com.neuedu.hisweb;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.neuedu.hisweb.entity.ConstantItem;
import com.neuedu.hisweb.entity.User;
import com.neuedu.hisweb.entity.vo.ConstantItemVo;
import com.neuedu.hisweb.mapper.ConstantItemMapper;
import com.neuedu.hisweb.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HisWebApplicationTests {
    String url="jdbc:mysql://127.0.0.1:3306/his?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true";
    String user="root";
    String pwd="111111";
    @Test
    public  void fastAutoGeneratorTest(){
        FastAutoGenerator.create(url,"root","111111")
                .globalConfig(builder -> {
                    builder.author("lynn") // 设置作者//开启 swagger 模式
                            .outputDir("D:\\ProjectSpace\\HIS\\his\\service\\src\\main\\java");// 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.neuedu.hisweb") // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,"D:\\ProjectSpace\\HIS\\his\\service\\src\\main\\java\\com\\neuedu\\hisweb\\mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("MedicalDisease") // 设置需要生成的表名
                            .addTablePrefix("t_","c_") ; // 设置过滤表前缀
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


    @Autowired
    private UserMapper userMapper;
    @Test
    public void MybatisTest(){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, 1);
        User user= userMapper.selectOne(wrapper);
        System.out.println(user.toString());
    }

    @Autowired
    private ConstantItemMapper constantItemMapper;
    @Test
    public void testMyPage(){
        Page<ConstantItemVo> query =new Page<>(1,10);

        Page<ConstantItemVo> page = constantItemMapper.selectPage(query,"骨","");

        System.out.println(page.toString());
    }

}
