package com.neuedu.hisweb.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.hisweb.entity.MedicalResult;
import com.neuedu.hisweb.mapper.MedicalResultMapper;
import com.neuedu.hisweb.service.IMedicalResultService;
import org.springframework.stereotype.Service;

@Service
public class MedicalResultServiceImpl extends ServiceImpl<MedicalResultMapper, MedicalResult> implements IMedicalResultService {
} 