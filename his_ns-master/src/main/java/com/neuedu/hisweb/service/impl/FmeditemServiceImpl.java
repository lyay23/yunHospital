package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.Fmeditem;
import com.neuedu.hisweb.entity.vo.FmeditemVo;
import com.neuedu.hisweb.mapper.FmeditemMapper;
import com.neuedu.hisweb.service.IFmeditemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-07-31
 */
@Service
public class FmeditemServiceImpl extends ServiceImpl<FmeditemMapper, Fmeditem> implements IFmeditemService {

    @Override
    public Page<FmeditemVo> selectPage(Page<FmeditemVo> page, String keyword, List<String> expClassIds, String dept) {
        return getBaseMapper().selectPage(page,keyword,expClassIds,dept);
    }

    @Override
    @Transactional
    public Integer importFmeditem(MultipartFile file) {
        try {
            // 1. 使用POI解析Excel文件
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            // 2. 准备一个列表来存储Fmeditem对象
            List<Fmeditem> list = new ArrayList<>();

            // 3. 遍历Excel的每一行（跳过标题行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                // 4. 将行数据转换为Fmeditem对象
                Fmeditem fmeditem = new Fmeditem();
                fmeditem.setItemCode(row.getCell(0).getStringCellValue());
                fmeditem.setItemName(row.getCell(1).getStringCellValue());
                fmeditem.setFormat(row.getCell(2).getStringCellValue());
                fmeditem.setPrice(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
                fmeditem.setExpClassID((int) row.getCell(4).getNumericCellValue());
                fmeditem.setDeptID((int) row.getCell(5).getNumericCellValue());
                fmeditem.setMnemonicCode(row.getCell(6).getStringCellValue());

                list.add(fmeditem);
            }

            // 5. 批量保存到数据库
            saveBatch(list);
            workbook.close();
            return list.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
