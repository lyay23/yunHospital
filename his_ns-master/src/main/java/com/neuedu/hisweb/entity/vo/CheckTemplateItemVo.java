package com.neuedu.hisweb.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neuedu.hisweb.entity.CheckTemplate;
import com.neuedu.hisweb.entity.Fmeditem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckTemplateItemVo extends CheckTemplate {
    private List<Integer> itemIds;
    private List<Fmeditem> items;

    @JsonProperty("doctorId")
    private Integer doctorId;
} 