package com.neuedu.hisweb.utils;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.annotation.FieldFill; // 引入正确的枚举类
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Scanner;

public class CodeGenerator {

    private static final String URL = "jdbc:mysql://localhost:3306/his02?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1";
    private static final String AUTHOR = "xj";
    private static final String PARENT_PACKAGE = "com.neuedu.hisweb";
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/java";

    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 获取需要生成的表名
        String tables = scanner("表名，多个英文逗号分割");

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            .outputDir(OUTPUT_DIR)
                            .enableFileOverride()
                            .disableOpenDir()
                            .dateType(DateType.ONLY_DATE)
                            .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper"
                            ));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables.split(","))
                            .entityBuilder()
                            .enableLombok()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .logicDeleteColumnName("deleted")
                            .versionColumnName("version")
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),  // 修改为正确的枚举值
                                    new Column("update_time", FieldFill.INSERT_UPDATE)  // 修改为正确的枚举值
                            )
                            .controllerBuilder()
                            .enableRestStyle()
                            .formatFileName("%sController")
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .mapperBuilder()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}