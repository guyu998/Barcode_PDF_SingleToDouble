package com.singletodouble.web.domain.base;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.singletodouble.common.annotation.Excel;
import com.singletodouble.common.annotation.Excel.ColumnType;
import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 【客户】对象 mo_customer
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoCustomer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 客户编码 */
    @Excel(name = "客户编码", cellType = ColumnType.STRING)
    private String customerCode;

    /** 客户简称 */
    @Excel(name = "客户名称", cellType = ColumnType.STRING)
    private String customerName;

    public void setCustomerCode(String customerCode) 
    {
        this.customerCode = customerCode;
    }

    public String getCustomerCode() 
    {
        return customerCode;
    }
    public void setCustomerName(String customerName) 
    {
        this.customerName = customerName;
    }

    public String getCustomerName() 
    {
        return customerName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("customerCode", getCustomerCode())
            .append("customerName", getCustomerName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
