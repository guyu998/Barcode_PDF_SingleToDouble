package com.singletodouble.web.domain.base;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 【生产商对应顾客详细信息】对象 mo_maker_customer_detail
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoMaterial extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 制造商编码 */
    private String makerBm;

    /** 制造商简称 */
    private String makerShortName;

    /** 物料编码 */
    private String makerMatNo;

    /** 物料简码 */
    private String makerBackNo;

    /** 物料名称 */
    private String makerMatName;

    /** 物料名称(英) */
    private String makerMatNameEn;

    /** 颜色代码 */
    private String makerColorCode;

    /** 颜色 */
    private String makerColor;

    /** 收容数 */
    private String makerSnp;

    /** 计量单位 */
    private String makerCalUnit;

    /** 采购单位 */
    private String makerBuyUnit;

    /** 规格型号 */
    private String makerSpecModel;

    /** 物料分类 */
    private String makerMatCatg;

    /** 左右件 */
    private String makerLeftRightFlag;

    /** 重保件 */
    private String makerImpProtectFlag;

    /** 车型 */
    private String makerCarModel;

    /** 车型用量 */
    private String makerCarPerQty;

    /** 库位 */
    private String makerPlacePos;

    /** 后工程 */
    private String makerPostProc;

    /** 包装编码 */
    private String makerPackBm;

    /** 托占比 */
    private String makerPackRate;

    /** 组合打托 */
    private String makerPackGroup;

    /** 托标准箱数 */
    private String makerTrayStdBoxQty;

    /** 初始库存类型 */
    private String makerInitStockType;

    /** 标签颜色 */
    private String makerLabelColor;

    /** 箱标签模板 */
    private String makerLabelTmpl;

    /** 物料标签模板 */
    private String makerMatLabelTmpl;

    /** 箱尾数模板 */
    private String makerModTmpl;

    /** 使用标识(0:未使用 1:使用中) */
    private Integer makerUseFlag;

    /** 线别 */
    private String makerLine;

    /** 预备1 */
    private String makerBackup1;

    /** 预备2 */
    private String makerBackup2;

    /** 预备3 */
    private String makerBackup3;

    /** 库存系数下限 */
    private String stockDown;

    /** 库存系数上限 */
    private String stockUp;

    /** 在库时长[h]下限 */
    private String holdTimeMin;

    /** 在库时长[h]上限 */
    private String holdTimeMax;

    /** 客户编码 */
    private String custBm;

    /** 客户简称 */
    private String custShortName;

    /** 物料编码 */
    private String custMatNo;

    /** 物料简码 */
    private String custBackNo;

    /** 物料名称 */
    private String custMatName;

    /** 物料名称(英) */
    private String custMatNameEn;

    /** 颜色代码 */
    private String custColorCode;

    /** 颜色 */
    private String custColor;

    /** 收容数 */
    private String custSnp;

    /** 计量单位 */
    private String custCalUnit;

    /** 采购单位 */
    private String custBuyUnit;

    /** 规格型号 */
    private String custSpecModel;

    /** 物料分类 */
    private String custMatCatg;

    /** 左右件 */
    private String custLeftRightFlag;

    /** 重保件 */
    private String custImpProtectFlag;

    /** 车型 */
    private String custCarModel;

    /** 车型用量 */
    private String custCarPerQty;

    /** 库位 */
    private String custPlacePos;

    /** 后工程 */
    private String custPostProc;

    /** 包装编码 */
    private String custPackBm;

    /** 托占比 */
    private String custPackRate;

    /** 组合打托 */
    private String custPackGroup;

    /** 标签颜色 */
    private String custLabelColor;

    /** 箱标签模板 */
    private String custLabelTmpl;

    /** 物料标签模板 */
    private String custMatLabelTmpl;

    /** 箱尾数模板 */
    private String custModTmpl;

    /** 使用标识(0:未使用 1:使用中) */
    private Integer custUseFlag;

    /** 预备1 */
    private String custBackup1;

    /** 预备2 */
    private String custBackup2;

    /** 预备3 */
    private String custBackup3;

    /** 备货L/T */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date prepareLeadTime;

    /** 出货L/T */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date sendLeadTime;

    /** 制造商日历 */
    private String makerCal;

    /** 制造商线别 */
    private String makerLineBm;

    /** 物料排序 */
    private String matOrder;
    
    private List<MoMaterialBom> bomList;

    public void setMakerBm(String makerBm) 
    {
        this.makerBm = makerBm;
    }

    public String getMakerBm() 
    {
        return makerBm;
    }
    public void setMakerShortName(String makerShortName) 
    {
        this.makerShortName = makerShortName;
    }

    public String getMakerShortName() 
    {
        return makerShortName;
    }
    public void setMakerMatNo(String makerMatNo) 
    {
        this.makerMatNo = makerMatNo;
    }

    public String getMakerMatNo() 
    {
        return makerMatNo;
    }
    public void setMakerBackNo(String makerBackNo) 
    {
        this.makerBackNo = makerBackNo;
    }

    public String getMakerBackNo() 
    {
        return makerBackNo;
    }
    public void setMakerMatName(String makerMatName) 
    {
        this.makerMatName = makerMatName;
    }

    public String getMakerMatName() 
    {
        return makerMatName;
    }
    public void setMakerMatNameEn(String makerMatNameEn) 
    {
        this.makerMatNameEn = makerMatNameEn;
    }

    public String getMakerMatNameEn() 
    {
        return makerMatNameEn;
    }
    public void setMakerColorCode(String makerColorCode) 
    {
        this.makerColorCode = makerColorCode;
    }

    public String getMakerColorCode() 
    {
        return makerColorCode;
    }
    public void setMakerColor(String makerColor) 
    {
        this.makerColor = makerColor;
    }

    public String getMakerColor() 
    {
        return makerColor;
    }
    public void setMakerSnp(String makerSnp) 
    {
        this.makerSnp = makerSnp;
    }

    public String getMakerSnp() 
    {
        return makerSnp;
    }
    public void setMakerCalUnit(String makerCalUnit) 
    {
        this.makerCalUnit = makerCalUnit;
    }

    public String getMakerCalUnit() 
    {
        return makerCalUnit;
    }
    public void setMakerBuyUnit(String makerBuyUnit) 
    {
        this.makerBuyUnit = makerBuyUnit;
    }

    public String getMakerBuyUnit() 
    {
        return makerBuyUnit;
    }
    public void setMakerSpecModel(String makerSpecModel) 
    {
        this.makerSpecModel = makerSpecModel;
    }

    public String getMakerSpecModel() 
    {
        return makerSpecModel;
    }
    public void setMakerMatCatg(String makerMatCatg) 
    {
        this.makerMatCatg = makerMatCatg;
    }

    public String getMakerMatCatg() 
    {
        return makerMatCatg;
    }
    public void setMakerLeftRightFlag(String makerLeftRightFlag) 
    {
        this.makerLeftRightFlag = makerLeftRightFlag;
    }

    public String getMakerLeftRightFlag() 
    {
        return makerLeftRightFlag;
    }
    public void setMakerImpProtectFlag(String makerImpProtectFlag) 
    {
        this.makerImpProtectFlag = makerImpProtectFlag;
    }

    public String getMakerImpProtectFlag() 
    {
        return makerImpProtectFlag;
    }
    public void setMakerCarModel(String makerCarModel) 
    {
        this.makerCarModel = makerCarModel;
    }

    public String getMakerCarModel() 
    {
        return makerCarModel;
    }
    public void setMakerCarPerQty(String makerCarPerQty) 
    {
        this.makerCarPerQty = makerCarPerQty;
    }

    public String getMakerCarPerQty() 
    {
        return makerCarPerQty;
    }
    public void setMakerPlacePos(String makerPlacePos) 
    {
        this.makerPlacePos = makerPlacePos;
    }

    public String getMakerPlacePos() 
    {
        return makerPlacePos;
    }
    public void setMakerPostProc(String makerPostProc) 
    {
        this.makerPostProc = makerPostProc;
    }

    public String getMakerPostProc() 
    {
        return makerPostProc;
    }
    public void setMakerPackBm(String makerPackBm) 
    {
        this.makerPackBm = makerPackBm;
    }

    public String getMakerPackBm() 
    {
        return makerPackBm;
    }
    public void setMakerPackRate(String makerPackRate) 
    {
        this.makerPackRate = makerPackRate;
    }

    public String getMakerPackRate() 
    {
        return makerPackRate;
    }
    public void setMakerPackGroup(String makerPackGroup) 
    {
        this.makerPackGroup = makerPackGroup;
    }

    public String getMakerPackGroup() 
    {
        return makerPackGroup;
    }
    public void setMakerTrayStdBoxQty(String makerTrayStdBoxQty) 
    {
        this.makerTrayStdBoxQty = makerTrayStdBoxQty;
    }

    public String getMakerTrayStdBoxQty() 
    {
        return makerTrayStdBoxQty;
    }
    public void setMakerInitStockType(String makerInitStockType) 
    {
        this.makerInitStockType = makerInitStockType;
    }

    public String getMakerInitStockType() 
    {
        return makerInitStockType;
    }
    public void setMakerLabelColor(String makerLabelColor) 
    {
        this.makerLabelColor = makerLabelColor;
    }

    public String getMakerLabelColor() 
    {
        return makerLabelColor;
    }
    public void setMakerLabelTmpl(String makerLabelTmpl) 
    {
        this.makerLabelTmpl = makerLabelTmpl;
    }

    public String getMakerLabelTmpl() 
    {
        return makerLabelTmpl;
    }
    public void setMakerMatLabelTmpl(String makerMatLabelTmpl) 
    {
        this.makerMatLabelTmpl = makerMatLabelTmpl;
    }

    public String getMakerMatLabelTmpl() 
    {
        return makerMatLabelTmpl;
    }
    public void setMakerModTmpl(String makerModTmpl) 
    {
        this.makerModTmpl = makerModTmpl;
    }

    public String getMakerModTmpl() 
    {
        return makerModTmpl;
    }
    public void setMakerUseFlag(Integer makerUseFlag) 
    {
        this.makerUseFlag = makerUseFlag;
    }

    public Integer getMakerUseFlag() 
    {
        return makerUseFlag;
    }
    public void setMakerLine(String makerLine) 
    {
        this.makerLine = makerLine;
    }

    public String getMakerLine() 
    {
        return makerLine;
    }
    public void setMakerBackup1(String makerBackup1) 
    {
        this.makerBackup1 = makerBackup1;
    }

    public String getMakerBackup1() 
    {
        return makerBackup1;
    }
    public void setMakerBackup2(String makerBackup2) 
    {
        this.makerBackup2 = makerBackup2;
    }

    public String getMakerBackup2() 
    {
        return makerBackup2;
    }
    public void setMakerBackup3(String makerBackup3) 
    {
        this.makerBackup3 = makerBackup3;
    }

    public String getMakerBackup3() 
    {
        return makerBackup3;
    }
    public void setStockDown(String stockDown) 
    {
        this.stockDown = stockDown;
    }

    public String getStockDown() 
    {
        return stockDown;
    }
    public void setStockUp(String stockUp) 
    {
        this.stockUp = stockUp;
    }

    public String getStockUp() 
    {
        return stockUp;
    }
    public void setHoldTimeMin(String holdTimeMin) 
    {
        this.holdTimeMin = holdTimeMin;
    }

    public String getHoldTimeMin() 
    {
        return holdTimeMin;
    }
    public void setHoldTimeMax(String holdTimeMax) 
    {
        this.holdTimeMax = holdTimeMax;
    }

    public String getHoldTimeMax() 
    {
        return holdTimeMax;
    }
    public void setCustBm(String custBm) 
    {
        this.custBm = custBm;
    }

    public String getCustBm() 
    {
        return custBm;
    }
    public void setCustShortName(String custShortName) 
    {
        this.custShortName = custShortName;
    }

    public String getCustShortName() 
    {
        return custShortName;
    }
    public void setCustMatNo(String custMatNo) 
    {
        this.custMatNo = custMatNo;
    }

    public String getCustMatNo() 
    {
        return custMatNo;
    }
    public void setCustBackNo(String custBackNo) 
    {
        this.custBackNo = custBackNo;
    }

    public String getCustBackNo() 
    {
        return custBackNo;
    }
    public void setCustMatName(String custMatName) 
    {
        this.custMatName = custMatName;
    }

    public String getCustMatName() 
    {
        return custMatName;
    }
    public void setCustMatNameEn(String custMatNameEn) 
    {
        this.custMatNameEn = custMatNameEn;
    }

    public String getCustMatNameEn() 
    {
        return custMatNameEn;
    }
    public void setCustColorCode(String custColorCode) 
    {
        this.custColorCode = custColorCode;
    }

    public String getCustColorCode() 
    {
        return custColorCode;
    }
    public void setCustColor(String custColor) 
    {
        this.custColor = custColor;
    }

    public String getCustColor() 
    {
        return custColor;
    }
    public void setCustSnp(String custSnp) 
    {
        this.custSnp = custSnp;
    }

    public String getCustSnp() 
    {
        return custSnp;
    }
    public void setCustCalUnit(String custCalUnit) 
    {
        this.custCalUnit = custCalUnit;
    }

    public String getCustCalUnit() 
    {
        return custCalUnit;
    }
    public void setCustBuyUnit(String custBuyUnit) 
    {
        this.custBuyUnit = custBuyUnit;
    }

    public String getCustBuyUnit() 
    {
        return custBuyUnit;
    }
    public void setCustSpecModel(String custSpecModel) 
    {
        this.custSpecModel = custSpecModel;
    }

    public String getCustSpecModel() 
    {
        return custSpecModel;
    }
    public void setCustMatCatg(String custMatCatg) 
    {
        this.custMatCatg = custMatCatg;
    }

    public String getCustMatCatg() 
    {
        return custMatCatg;
    }
    public void setCustLeftRightFlag(String custLeftRightFlag) 
    {
        this.custLeftRightFlag = custLeftRightFlag;
    }

    public String getCustLeftRightFlag() 
    {
        return custLeftRightFlag;
    }
    public void setCustImpProtectFlag(String custImpProtectFlag) 
    {
        this.custImpProtectFlag = custImpProtectFlag;
    }

    public String getCustImpProtectFlag() 
    {
        return custImpProtectFlag;
    }
    public void setCustCarModel(String custCarModel) 
    {
        this.custCarModel = custCarModel;
    }

    public String getCustCarModel() 
    {
        return custCarModel;
    }
    public void setCustCarPerQty(String custCarPerQty) 
    {
        this.custCarPerQty = custCarPerQty;
    }

    public String getCustCarPerQty() 
    {
        return custCarPerQty;
    }
    public void setCustPlacePos(String custPlacePos) 
    {
        this.custPlacePos = custPlacePos;
    }

    public String getCustPlacePos() 
    {
        return custPlacePos;
    }
    public void setCustPostProc(String custPostProc) 
    {
        this.custPostProc = custPostProc;
    }

    public String getCustPostProc() 
    {
        return custPostProc;
    }
    public void setCustPackBm(String custPackBm) 
    {
        this.custPackBm = custPackBm;
    }

    public String getCustPackBm() 
    {
        return custPackBm;
    }
    public void setCustPackRate(String custPackRate) 
    {
        this.custPackRate = custPackRate;
    }

    public String getCustPackRate() 
    {
        return custPackRate;
    }
    public void setCustPackGroup(String custPackGroup) 
    {
        this.custPackGroup = custPackGroup;
    }

    public String getCustPackGroup() 
    {
        return custPackGroup;
    }
    public void setCustLabelColor(String custLabelColor) 
    {
        this.custLabelColor = custLabelColor;
    }

    public String getCustLabelColor() 
    {
        return custLabelColor;
    }
    public void setCustLabelTmpl(String custLabelTmpl) 
    {
        this.custLabelTmpl = custLabelTmpl;
    }

    public String getCustLabelTmpl() 
    {
        return custLabelTmpl;
    }
    public void setCustMatLabelTmpl(String custMatLabelTmpl) 
    {
        this.custMatLabelTmpl = custMatLabelTmpl;
    }

    public String getCustMatLabelTmpl() 
    {
        return custMatLabelTmpl;
    }
    public void setCustModTmpl(String custModTmpl) 
    {
        this.custModTmpl = custModTmpl;
    }

    public String getCustModTmpl() 
    {
        return custModTmpl;
    }
    public void setCustUseFlag(Integer custUseFlag) 
    {
        this.custUseFlag = custUseFlag;
    }

    public Integer getCustUseFlag() 
    {
        return custUseFlag;
    }
    public void setCustBackup1(String custBackup1) 
    {
        this.custBackup1 = custBackup1;
    }

    public String getCustBackup1() 
    {
        return custBackup1;
    }
    public void setCustBackup2(String custBackup2) 
    {
        this.custBackup2 = custBackup2;
    }

    public String getCustBackup2() 
    {
        return custBackup2;
    }
    public void setCustBackup3(String custBackup3) 
    {
        this.custBackup3 = custBackup3;
    }

    public String getCustBackup3() 
    {
        return custBackup3;
    }
    public void setPrepareLeadTime(Date prepareLeadTime) 
    {
        this.prepareLeadTime = prepareLeadTime;
    }

    public Date getPrepareLeadTime() 
    {
        return prepareLeadTime;
    }
    public void setSendLeadTime(Date sendLeadTime) 
    {
        this.sendLeadTime = sendLeadTime;
    }

    public Date getSendLeadTime() 
    {
        return sendLeadTime;
    }
    public void setMakerCal(String makerCal) 
    {
        this.makerCal = makerCal;
    }

    public String getMakerCal() 
    {
        return makerCal;
    }
    public void setMakerLineBm(String makerLineBm) 
    {
        this.makerLineBm = makerLineBm;
    }

    public String getMakerLineBm() 
    {
        return makerLineBm;
    }
    public void setMatOrder(String matOrder) 
    {
        this.matOrder = matOrder;
    }

    public String getMatOrder() 
    {
        return matOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("makerBm", getMakerBm())
            .append("makerShortName", getMakerShortName())
            .append("makerMatNo", getMakerMatNo())
            .append("makerBackNo", getMakerBackNo())
            .append("makerMatName", getMakerMatName())
            .append("makerMatNameEn", getMakerMatNameEn())
            .append("makerColorCode", getMakerColorCode())
            .append("makerColor", getMakerColor())
            .append("makerSnp", getMakerSnp())
            .append("makerCalUnit", getMakerCalUnit())
            .append("makerBuyUnit", getMakerBuyUnit())
            .append("makerSpecModel", getMakerSpecModel())
            .append("makerMatCatg", getMakerMatCatg())
            .append("makerLeftRightFlag", getMakerLeftRightFlag())
            .append("makerImpProtectFlag", getMakerImpProtectFlag())
            .append("makerCarModel", getMakerCarModel())
            .append("makerCarPerQty", getMakerCarPerQty())
            .append("makerPlacePos", getMakerPlacePos())
            .append("makerPostProc", getMakerPostProc())
            .append("makerPackBm", getMakerPackBm())
            .append("makerPackRate", getMakerPackRate())
            .append("makerPackGroup", getMakerPackGroup())
            .append("makerTrayStdBoxQty", getMakerTrayStdBoxQty())
            .append("makerInitStockType", getMakerInitStockType())
            .append("makerLabelColor", getMakerLabelColor())
            .append("makerLabelTmpl", getMakerLabelTmpl())
            .append("makerMatLabelTmpl", getMakerMatLabelTmpl())
            .append("makerModTmpl", getMakerModTmpl())
            .append("makerUseFlag", getMakerUseFlag())
            .append("makerLine", getMakerLine())
            .append("makerBackup1", getMakerBackup1())
            .append("makerBackup2", getMakerBackup2())
            .append("makerBackup3", getMakerBackup3())
            .append("stockDown", getStockDown())
            .append("stockUp", getStockUp())
            .append("holdTimeMin", getHoldTimeMin())
            .append("holdTimeMax", getHoldTimeMax())
            .append("custBm", getCustBm())
            .append("custShortName", getCustShortName())
            .append("custMatNo", getCustMatNo())
            .append("custBackNo", getCustBackNo())
            .append("custMatName", getCustMatName())
            .append("custMatNameEn", getCustMatNameEn())
            .append("custColorCode", getCustColorCode())
            .append("custColor", getCustColor())
            .append("custSnp", getCustSnp())
            .append("custCalUnit", getCustCalUnit())
            .append("custBuyUnit", getCustBuyUnit())
            .append("custSpecModel", getCustSpecModel())
            .append("custMatCatg", getCustMatCatg())
            .append("custLeftRightFlag", getCustLeftRightFlag())
            .append("custImpProtectFlag", getCustImpProtectFlag())
            .append("custCarModel", getCustCarModel())
            .append("custCarPerQty", getCustCarPerQty())
            .append("custPlacePos", getCustPlacePos())
            .append("custPostProc", getCustPostProc())
            .append("custPackBm", getCustPackBm())
            .append("custPackRate", getCustPackRate())
            .append("custPackGroup", getCustPackGroup())
            .append("custLabelColor", getCustLabelColor())
            .append("custLabelTmpl", getCustLabelTmpl())
            .append("custMatLabelTmpl", getCustMatLabelTmpl())
            .append("custModTmpl", getCustModTmpl())
            .append("custUseFlag", getCustUseFlag())
            .append("custBackup1", getCustBackup1())
            .append("custBackup2", getCustBackup2())
            .append("custBackup3", getCustBackup3())
            .append("prepareLeadTime", getPrepareLeadTime())
            .append("sendLeadTime", getSendLeadTime())
            .append("makerCal", getMakerCal())
            .append("makerLineBm", getMakerLineBm())
            .append("matOrder", getMatOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }

	public List<MoMaterialBom> getBomList() {
		return bomList;
	}

	public void setBomList(List<MoMaterialBom> bomList) {
		this.bomList = bomList;
	}
}
