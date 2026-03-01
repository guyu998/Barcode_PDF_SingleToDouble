package com.singletodouble.web.domain.base;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 【数据序列号记录表】对象 mo_serial
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoSerial extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 客户编码 */
    private String custBm;

    /** 序列号(1~9999) */
    private Integer serialNumber;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCustBm(String custBm) 
    {
        this.custBm = custBm;
    }

    public String getCustBm() 
    {
        return custBm;
    }
    public void setSerialNumber(Integer serialNumber) 
    {
        this.serialNumber = serialNumber;
    }

    public Integer getSerialNumber() 
    {
        return serialNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("custBm", getCustBm())
            .append("serialNumber", getSerialNumber())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
