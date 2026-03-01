package com.singletodouble.web.domain.task;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 【数据处理失败日志】对象 mo_task_detail_log
 * 
 * @author sunyi
 * @date 2026-02-08
 */
public class MoTaskMaterialLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private String taskId;

    /** 数据标号 */
    private Integer dataNo;

    /** 处理失败原因 */
    private String log;

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
    public void setLog(String log) 
    {
        this.log = log;
    }

    public String getLog() 
    {
        return log;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("dataNo", getDataNo())
            .append("log", getLog())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
