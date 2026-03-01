package com.singletodouble.web.domain.base;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 【生产商对应客户的BOM信息】对象 mo_maker_customer_bom
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoMaterialBom extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 物料编码（制造商） */
    private String makerMatNo;

    /** 物料编码（顾客） */
    private String custMatNo;
    
    /** 颜色代码 */
    private String custColorCode;

    /** 顺番号 */
    private Integer orderNo;

    /** 部品名 */
    private String partName;

    /** 背番号 */
    private String backNo;

    public void setMakerMatNo(String makerMatNo) 
    {
        this.makerMatNo = makerMatNo;
    }

    public String getMakerMatNo() 
    {
        return makerMatNo;
    }
    public void setCustMatNo(String custMatNo) 
    {
        this.custMatNo = custMatNo;
    }

    public String getCustMatNo() 
    {
        return custMatNo;
    }
    public void setOrderNo(Integer orderNo) 
    {
        this.orderNo = orderNo;
    }

    public Integer getOrderNo() 
    {
        return orderNo;
    }
    public void setPartName(String partName) 
    {
        this.partName = partName;
    }

    public String getPartName() 
    {
        return partName;
    }
    public void setBackNo(String backNo) 
    {
        this.backNo = backNo;
    }

    public String getBackNo() 
    {
        return backNo;
    }

	public String getCustColorCode() {
		return custColorCode;
	}

	public void setCustColorCode(String custColorCode) {
		this.custColorCode = custColorCode;
	}
	
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("makerMatNo", getMakerMatNo())
            .append("custMatNo", getCustMatNo())
            .append("custColorCode", getCustColorCode())
            .append("orderNo", getOrderNo())
            .append("partName", getPartName())
            .append("backNo", getBackNo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
