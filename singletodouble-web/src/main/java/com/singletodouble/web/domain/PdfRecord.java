package com.singletodouble.web.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.singletodouble.common.core.domain.BaseEntity;

public class PdfRecord extends BaseEntity {

	private static final long serialVersionUID = -7101310736954487377L;

	/**
	 * 背番
	 */
	private String backNo;
	
	/**
	 * TR纳入日
	 */
	private String trEntryDate;
	
	/**
	 * SEQ
	 */
	private String seqNo;
	
	/**
	 * 订货号
	 */
	private String orderCode;
	
	/**
	 * 订单NO.
	 */
	private String orderNo;
	
	/**
	 * 总箱数
	 */
	private String boxCount;
	
	/**
	 * 纳入指示数量
	 */
	private String entryTargetBoxCount;	
	
	/**
	 * 发行日
	 */
	private String publishDate;	
	
	/**
	 * 件号
	 */
	private String keyNo;
	
	/**
	 * TR品番
	 */
	private String trNo;
	
	/**
	 * 品名
	 */
	private String ObjectName;
	
	/**
	 * 顾客番号
	 */
	private String customNo;
	
	/**
	 * 收容数
	 */
	private String collectCount;
	
	/**
	 * 二维码
	 */
	private String QrImage;
	
	/**
	 * W/C
	 */
	private String wc;
	
	/**
	 * 库位
	 */
	private String location;
	
	/**
	 * 箱种
	 */
	private String boxType;
	
	/**
	 * 线别
	 */
	private String lineName;
	
	/**
	 * 批次号
	 */
	private String batchNo;
	
	/**
	 * 公司名
	 */
	private String corpName;
	
	/**
	 * KD No.
	 */
	private String kdNo;
	
	/**
	 * 受入场所编号
	 */
	private String entryPlaceNo;
	
	/**
	 * 纳入指示日期
	 */
	private String entryDate;
	
	/**
	 * 便次
	 */
	private String tipNo;
	
	/**
	 * 时间
	 */
	private String entryTime;
	
	/**
	 * 出货日
	 */
	private String outDate;	
	
	/**
	 * 托盘No.
	 */
	private String trayNo;	
	
	/**
	 * 客先使用栏
	 */
	private String memo;

	public String getBackNo() {
		return backNo;
	}

	public void setBackNo(String backNo) {
		this.backNo = backNo;
	}

	public String getTrEntryDate() {
		return trEntryDate;
	}

	public void setTrEntryDate(String trEntryDate) {
		this.trEntryDate = trEntryDate;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getBoxCount() {
		return boxCount;
	}

	public void setBoxCount(String boxCount) {
		this.boxCount = boxCount;
	}

	public String getEntryTargetBoxCount() {
		return entryTargetBoxCount;
	}

	public void setEntryTargetBoxCount(String entryTargetBoxCount) {
		this.entryTargetBoxCount = entryTargetBoxCount;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getKeyNo() {
		return keyNo;
	}

	public void setKeyNo(String keyNo) {
		this.keyNo = keyNo;
	}

	public String getTrNo() {
		return trNo;
	}

	public void setTrNo(String trNo) {
		this.trNo = trNo;
	}

	public String getObjectName() {
		return ObjectName;
	}

	public void setObjectName(String objectName) {
		ObjectName = objectName;
	}

	public String getCustomNo() {
		return customNo;
	}

	public void setCustomNo(String customNo) {
		this.customNo = customNo;
	}

	public String getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(String collectCount) {
		this.collectCount = collectCount;
	}

	public String getQrImage() {
		return QrImage;
	}

	public void setQrImage(String qrImage) {
		QrImage = qrImage;
	}

	public String getWc() {
		return wc;
	}

	public void setWc(String wc) {
		this.wc = wc;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public String getLineName() {
		return lineName;
	}

	public void setLineName(String lineName) {
		this.lineName = lineName;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getKdNo() {
		return kdNo;
	}

	public void setKdNo(String kdNo) {
		this.kdNo = kdNo;
	}

	public String getEntryPlaceNo() {
		return entryPlaceNo;
	}

	public void setEntryPlaceNo(String entryPlaceNo) {
		this.entryPlaceNo = entryPlaceNo;
	}

	public String getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}

	public String getTipNo() {
		return tipNo;
	}

	public void setTipNo(String tipNo) {
		this.tipNo = tipNo;
	}

	public String getEntryTime() {
		return entryTime;
	}

	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}

	public String getOutDate() {
		return outDate;
	}

	public void setOutDate(String outDate) {
		this.outDate = outDate;
	}

	public String getTrayNo() {
		return trayNo;
	}

	public void setTrayNo(String trayNo) {
		this.trayNo = trayNo;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}	
	
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("backNo", getBackNo())
            .append("trEntryDate", getTrEntryDate())
            .append("seqNo", getSeqNo())
            .append("orderCode", getOrderCode())
            .append("orderNo", getOrderNo())
            .append("boxCount", getBoxCount())
            .append("entryTargetBoxCount", getEntryTargetBoxCount())
            .append("publishDate", getPublishDate())
            .append("keyNo", getKeyNo())
            .append("trNo", getTrNo())
            .append("ObjectName", getObjectName())
            .append("customNo", getCustomNo())
            .append("collectCount", getCollectCount())
            .append("wc", getWc())
            .append("location", getLocation())
            .append("boxType", getBoxType())
            .append("lineName", getLineName())
            .append("batchNo", getBatchNo())
            .append("corpName", getCorpName())
            .append("kdNo", getKdNo())
            .append("entryPlaceNo", getEntryPlaceNo())
            .append("entryDate", getEntryDate())
            .append("tipNo", getTipNo())
            .append("entryTime", getEntryTime())
            .append("outDate", getOutDate())
            .append("trayNo", getTrayNo())
            .append("memo", getMemo())
            .toString();
    }

}
