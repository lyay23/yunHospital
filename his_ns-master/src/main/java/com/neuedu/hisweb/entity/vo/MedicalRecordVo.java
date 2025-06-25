package com.neuedu.hisweb.entity.vo;

import com.neuedu.hisweb.entity.MedicalRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author lynn
 * @since 2023-11-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MedicalRecordVo extends MedicalRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<MedicalDiseaseVo> medicalDiseases;

}
