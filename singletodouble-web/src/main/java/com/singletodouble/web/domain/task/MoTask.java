package com.singletodouble.web.domain.task;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.singletodouble.common.annotation.Excel;
import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 【生成pdf任务】对象 mo_task
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** yyyy-MM-dd-### */
    private String id;

    /** 客户编码 */
    private String custBm;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 上传文件数量 */
    private Integer fileCount;

    /** 页数总量 */
    private Integer pageCount;

    /** 待处理数据量 */
    private Integer dataCount;

    /** 处理状态(0:待处理 1:处理中 2:处理完成 3:部分失败 4:全部失败 9:异常结束） */
    private Integer handleStatus;

    /** 开始处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startHandleTime;

    /** 结束处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endHandleTime;

    /** 消耗时间（秒） */
    private Integer costSec;

    /** 成功处理数据量 */
    private Integer successCount;

    /** 打印类别(1:普通 2:穿透) */
    private Integer printType;

    /** 输出文件路径 */
    @Excel(name = "输出文件路径")
    private String outputFile;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
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
    public void setUploadTime(Date uploadTime) 
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime() 
    {
        return uploadTime;
    }
    public void setFileCount(Integer fileCount) 
    {
        this.fileCount = fileCount;
    }

    public Integer getFileCount() 
    {
        return fileCount;
    }
    public void setPageCount(Integer pageCount) 
    {
        this.pageCount = pageCount;
    }

    public Integer getPageCount() 
    {
        return pageCount;
    }
    public void setDataCount(Integer dataCount) 
    {
        this.dataCount = dataCount;
    }

    public Integer getDataCount() 
    {
        return dataCount;
    }
    public void setHandleStatus(Integer handleStatus) 
    {
        this.handleStatus = handleStatus;
    }

    public Integer getHandleStatus() 
    {
        return handleStatus;
    }
    public void setStartHandleTime(Date startHandleTime) 
    {
        this.startHandleTime = startHandleTime;
    }

    public Date getStartHandleTime() 
    {
        return startHandleTime;
    }
    public void setEndHandleTime(Date endHandleTime) 
    {
        this.endHandleTime = endHandleTime;
    }

    public Date getEndHandleTime() 
    {
        return endHandleTime;
    }
    public void setCostSec(Integer costSec) 
    {
        this.costSec = costSec;
    }

    public Integer getCostSec() 
    {
        return costSec;
    }
    public void setSuccessCount(Integer successCount) 
    {
        this.successCount = successCount;
    }

    public Integer getSuccessCount() 
    {
        return successCount;
    }
    public void setPrintType(Integer printType) 
    {
        this.printType = printType;
    }

    public Integer getPrintType() 
    {
        return printType;
    }
    public void setOutputFile(String outputFile) 
    {
        this.outputFile = outputFile;
    }

    public String getOutputFile() 
    {
        return outputFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("custBm", getCustBm())
            .append("uploadTime", getUploadTime())
            .append("fileCount", getFileCount())
            .append("pageCount", getPageCount())
            .append("dataCount", getDataCount())
            .append("handleStatus", getHandleStatus())
            .append("startHandleTime", getStartHandleTime())
            .append("endHandleTime", getEndHandleTime())
            .append("costSec", getCostSec())
            .append("successCount", getSuccessCount())
            .append("printType", getPrintType())
            .append("outputFile", getOutputFile())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
