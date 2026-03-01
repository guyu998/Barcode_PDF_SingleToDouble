package com.singletodouble.web.domain.task;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.singletodouble.common.core.domain.BaseEntity;
import com.singletodouble.web.domain.base.MoMaterialBom;

/**
 * 【客户pdf的数据和生成pdf的序列号】对象 mo_task_detail
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoTaskMaterial extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private String taskId;

    /** 数据标号 */
    private Integer dataNo;

    /** 序列号（1~9999） */
    private Integer serialNumber;

    /** 背番 */
    private String backNo;

    /** TR纳入日 */
    private String trEntryDate;

    /** SEQ */
    private String seqNo;

    /** 订货号 */
    private String orderCode;

    /** 订单NO. */
    private String orderNo;

    /** 总箱数 */
    private String boxCount;

    /** 纳入指示数量 */
    private String entryTargetBoxCount;

    /** 发行日 */
    private String publishDate;

    /** 物料编码(顾客) */
    private String custMatNo;

    /** 物料编码(制造商)/TR品番 */
    private String makerMatNo;

    /** 品名 */
    private String custMatName;

    /** 物料简码 */
    private String custBackNo;

    /** 收容数 */
    private String custSnp;

    /** 二维码（顾客) */
    private byte[] custQrImage;

    /** W/C */
    private String custWc;

    /** 库位 */
    private String custPlacePos;

    /** 箱种 */
    private String custBoxType;

    /** 线别 */
    private String lineName;

    /** 批次号 */
    private String batchNo;

    /** 顾客名 */
    private String custName;
    
    /** 颜色编码 */
    private String custColorCode;

    /** KD NO. */
    private String kdNo;

    /** 受入场所编号 */
    private String entryPlaceNo;

    /** 纳入指示日期 */
    private String entryDate;

    /** 便次 */
    private String tipNo;

    /** 时间 */
    private String entryTime;

    /** 出货日 */
    private String outDate;

    /** 托盘No */
    private String trayNo;

    /** 客先使用栏 */
    private String memo;
    
    private List<MoMaterialBom> bomList;

    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setDataNo(Integer dataNo) 
    {
        this.dataNo = dataNo;
    }

    public Integer getDataNo() 
    {
        return dataNo;
    }
    public void setSerialNumber(Integer serialNumber) 
    {
        this.serialNumber = serialNumber;
    }

    public Integer getSerialNumber() 
    {
        return serialNumber;
    }
    public void setBackNo(String backNo) 
    {
        this.backNo = backNo;
    }

    public String getBackNo() 
    {
        return backNo;
    }
    public void setTrEntryDate(String trEntryDate) 
    {
        this.trEntryDate = trEntryDate;
    }

    public String getTrEntryDate() 
    {
        return trEntryDate;
    }
    public void setSeqNo(String seqNo) 
    {
        this.seqNo = seqNo;
    }

    public String getSeqNo() 
    {
        return seqNo;
    }
    public void setOrderCode(String orderCode) 
    {
        this.orderCode = orderCode;
    }

    public String getOrderCode() 
    {
        return orderCode;
    }
    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
    }
    public void setBoxCount(String boxCount) 
    {
        this.boxCount = boxCount;
    }

    public String getBoxCount() 
    {
        return boxCount;
    }
    public void setEntryTargetBoxCount(String entryTargetBoxCount) 
    {
        this.entryTargetBoxCount = entryTargetBoxCount;
    }

    public String getEntryTargetBoxCount() 
    {
        return entryTargetBoxCount;
    }
    public void setPublishDate(String publishDate) 
    {
        this.publishDate = publishDate;
    }

    public String getPublishDate() 
    {
        return publishDate;
    }
    public void setCustMatNo(String custMatNo) 
    {
        this.custMatNo = custMatNo;
    }

    public String getCustMatNo() 
    {
        return custMatNo;
    }
    public void setMakerMatNo(String makerMatNo) 
    {
        this.makerMatNo = makerMatNo;
    }

    public String getMakerMatNo() 
    {
        return makerMatNo;
    }
    public void setCustMatName(String custMatName) 
    {
        this.custMatName = custMatName;
    }

    public String getCustMatName() 
    {
        return custMatName;
    }
    public void setCustBackNo(String custBackNo) 
    {
        this.custBackNo = custBackNo;
    }

    public String getCustBackNo() 
    {
        return custBackNo;
    }
    public void setCustSnp(String custSnp) 
    {
        this.custSnp = custSnp;
    }

    public String getCustSnp() 
    {
        return custSnp;
    }
    public void setCustQrImage(byte[] custQrImage) 
    {
        this.custQrImage = custQrImage;
    }

    public byte[] getCustQrImage() 
    {
        return custQrImage;
    }
    public void setCustWc(String custWc) 
    {
        this.custWc = custWc;
    }

    public String getCustWc() 
    {
        return custWc;
    }
    public void setCustPlacePos(String custPlacePos) 
    {
        this.custPlacePos = custPlacePos;
    }

    public String getCustPlacePos() 
    {
        return custPlacePos;
    }
    public void setCustBoxType(String custBoxType) 
    {
        this.custBoxType = custBoxType;
    }

    public String getCustBoxType() 
    {
        return custBoxType;
    }
    public void setLineName(String lineName) 
    {
        this.lineName = lineName;
    }

    public String getLineName() 
    {
        return lineName;
    }
    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }
    public void setCustName(String custName) 
    {
        this.custName = custName;
    }

    public String getCustName() 
    {
        return custName;
    }
    public void setKdNo(String kdNo) 
    {
        this.kdNo = kdNo;
    }

    public String getKdNo() 
    {
        return kdNo;
    }
    public void setEntryPlaceNo(String entryPlaceNo) 
    {
        this.entryPlaceNo = entryPlaceNo;
    }

    public String getEntryPlaceNo() 
    {
        return entryPlaceNo;
    }
    public void setEntryDate(String entryDate) 
    {
        this.entryDate = entryDate;
    }

    public String getEntryDate() 
    {
        return entryDate;
    }
    public void setTipNo(String tipNo) 
    {
        this.tipNo = tipNo;
    }

    public String getTipNo() 
    {
        return tipNo;
    }
    public void setEntryTime(String entryTime) 
    {
        this.entryTime = entryTime;
    }

    public String getEntryTime() 
    {
        return entryTime;
    }
    public void setOutDate(String outDate) 
    {
        this.outDate = outDate;
    }

    public String getOutDate() 
    {
        return outDate;
    }
    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("dataNo", getDataNo())
            .append("serialNumber", getSerialNumber())
            .append("backNo", getBackNo())
            .append("trEntryDate", getTrEntryDate())
            .append("seqNo", getSeqNo())
            .append("orderCode", getOrderCode())
            .append("orderNo", getOrderNo())
            .append("boxCount", getBoxCount())
            .append("entryTargetBoxCount", getEntryTargetBoxCount())
            .append("publishDate", getPublishDate())
            .append("custMatNo", getCustMatNo())
            .append("makerMatNo", getMakerMatNo())
            .append("custMatName", getCustMatName())
            .append("custBackNo", getCustBackNo())
            .append("custSnp", getCustSnp())
            .append("custQrImage", getCustQrImage())
            .append("custWc", getCustWc())
            .append("custPlacePos", getCustPlacePos())
            .append("custBoxType", getCustBoxType())
            .append("lineName", getLineName())
            .append("batchNo", getBatchNo())
            .append("custName", getCustName())
            .append("custColorCode", getCustColorCode())
            .append("kdNo", getKdNo())
            .append("entryPlaceNo", getEntryPlaceNo())
            .append("entryDate", getEntryDate())
            .append("tipNo", getTipNo())
            .append("entryTime", getEntryTime())
            .append("outDate", getOutDate())
            .append("memo", getMemo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }

	public String getCustColorCode() {
		return custColorCode;
	}

	public void setCustColorCode(String custColorCode) {
		this.custColorCode = custColorCode;
	}

	public List<MoMaterialBom> getBomList() {
		return bomList;
	}

	public void setBomList(List<MoMaterialBom> bomList) {
		this.bomList = bomList;
	}

	public String getTrayNo() {
		return trayNo;
	}

	public void setTrayNo(String trayNo) {
		this.trayNo = trayNo;
	}
}
