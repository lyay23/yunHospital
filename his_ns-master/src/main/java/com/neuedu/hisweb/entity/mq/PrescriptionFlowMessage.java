package com.neuedu.hisweb.entity.mq;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionFlowMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Integer> prescriptionIds;
    private Integer state;
    private Integer userId;
    private LocalDateTime time;
}
