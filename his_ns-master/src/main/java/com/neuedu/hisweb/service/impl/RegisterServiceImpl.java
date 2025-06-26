package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.hisweb.entity.ConstantType;
import com.neuedu.hisweb.entity.Invoice;
import com.neuedu.hisweb.entity.Patientcosts;
import com.neuedu.hisweb.entity.Register;
import com.neuedu.hisweb.entity.vo.RegParam;
import com.neuedu.hisweb.entity.vo.RegisterVo;
import com.neuedu.hisweb.mapper.InvoiceMapper;
import com.neuedu.hisweb.mapper.PatientcostsMapper;
import com.neuedu.hisweb.mapper.RegisterMapper;
import com.neuedu.hisweb.mapper.SchedulingMapper;
import com.neuedu.hisweb.service.IRegisterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.service.ISchedulingService;
import com.neuedu.hisweb.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lynn
 * @since 2023-08-09
 */
@Service
@Transactional
public class RegisterServiceImpl extends ServiceImpl<RegisterMapper, Register> implements IRegisterService {
    @Autowired
    private InvoiceMapper invoiceMapper;
    @Autowired
    private PatientcostsMapper patientcostsMapper;
    @Autowired
    private SchedulingMapper schedulingMapper;

    /**
     * *挂号
     * @param param
     * @return
     */
    @Override
    public boolean saveRegister(RegParam param) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date_str=df.format(new Date()).toString();
        Register register=param.getRegister();
        register.setRegistTime(date_str);
        register.setVisitDate(date_str);//挂号时间
        register.setVisitState(1);//本次看诊状态
        register.setCaseNumber(String.valueOf(getBaseMapper().getMaxCaseNumber()+1));
        int rs=getBaseMapper().insert(register);

        Invoice invoice=new Invoice();
        invoice.setInvoiceNum(Utils.getInvoiceNum());
        invoice.setMoney(param.getScheduling().getRegistFee());
        invoice.setState(3);
        invoice.setCreationTime(register.getRegistTime());
        invoice.setUserID(register.getUserID());
        invoice.setRegistID(register.getId());
        invoice.setFeeType(register.getSettleID());
        rs+=invoiceMapper.insert(invoice);

        Patientcosts patientcosts=new Patientcosts();
        patientcosts.setRegistID(register.getId());
        patientcosts.setInvoiceID(invoice.getId());
        patientcosts.setName("挂号费");
        patientcosts.setPrice(param.getScheduling().getRegistFee());
        patientcosts.setDeptID(register.getDeptID());
        patientcosts.setItemID(1);
        patientcosts.setItemType(1);
        patientcosts.setAmount(1);
        patientcosts.setCreatetime(register.getRegistTime());
        patientcosts.setCreateOperID(register.getRegisterID());
        patientcosts.setPayTime(register.getRegistTime());
        patientcosts.setRegisterID(register.getRegisterID());
        patientcosts.setFeeType(register.getSettleID());
        rs+=patientcostsMapper.insert(patientcosts);

        rs+=schedulingMapper.updateByRegister(param.getScheduling().getId());


        return rs==4?true:false;
    }

    /**
     * *分页查询
     * @param page
     * @param deptId
     * @param keyword
     * @param regDate
     * @return
     */
    @Override
    public Page<RegisterVo> selectPage(Page<RegisterVo> page,Integer deptId,Integer docId,Integer state,String keyword,String regDate) {
        return getBaseMapper().selectPage(page,deptId,docId,state,keyword,regDate);
    }

    /**
     * *退号
     * @param
     * @return
     */
    @Override
    public boolean updateRegisterState(RegParam param) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date_str=df.format(new Date()).toString();

        RegisterVo register=param.getRegisterVo();

        //退发票
        LambdaQueryWrapper<Invoice> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Invoice::getRegistID,register.getId())
                .eq(Invoice::getState,3);
        Invoice oldInvoice=invoiceMapper.selectOne(queryWrapper);

        Invoice invoice=new Invoice();
        invoice.setMoney(oldInvoice.getMoney());
        invoice.setState(7);//发票冲红状态==7
        invoice.setCreationTime(date_str);
        invoice.setUserID(param.getUserVo().getId());
        invoice.setRegistID(register.getId());
        invoice.setInvoiceNum(oldInvoice.getInvoiceNum());
        invoice.setFeeType(register.getSettleID());
        invoice.setBack(String.valueOf(oldInvoice.getId()));
        int rs= invoiceMapper.insert(invoice);

        //新增操作记录
        LambdaQueryWrapper<Patientcosts> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Patientcosts::getRegistID,register.getId())
                .eq(Patientcosts::getInvoiceID,oldInvoice.getId());
        Patientcosts oldPatie=patientcostsMapper.selectOne(lambdaQueryWrapper);
        Patientcosts patientcosts=new Patientcosts();
        patientcosts.setRegistID(register.getId());
        patientcosts.setInvoiceID(invoice.getId());
        patientcosts.setItemID(1);
        patientcosts.setItemType(1);
        patientcosts.setName("挂号费");
        patientcosts.setPrice(invoice.getMoney());
        patientcosts.setAmount(-1);
        patientcosts.setDeptID(register.getDeptID());
        patientcosts.setCreatetime(date_str);
        patientcosts.setCreateOperID(register.getRegisterID());
        patientcosts.setRegisterID(param.getUserVo().getId());
        patientcosts.setPayTime(date_str);
        patientcosts.setFeeType(register.getSettleID());
        patientcosts.setBackID(oldPatie.getId());
        rs+=patientcostsMapper.insert(patientcosts);

        //修改挂号记录
        rs+=getBaseMapper().updateVisitState(register.getId(),4);
        rs+=schedulingMapper.updateByBackRegister(register.getVisitDate(),register.getNoon(),register.getUserID());

        return true;
    }

    @Override
    public boolean updateVisitState(Integer id, Integer state) {
        Register register = new Register();
        register.setId(id);
        register.setVisitState(state);
        return this.updateById(register);
    }
}
