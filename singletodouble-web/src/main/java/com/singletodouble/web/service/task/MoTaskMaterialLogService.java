package com.singletodouble.web.service.task;

import java.util.List;

import com.singletodouble.web.domain.task.MoTaskMaterialLog;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoTaskMaterialLogService {
    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoTaskMaterialLog searchDataById(String taskId, int dataNo);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoTaskMaterialLog> searchList(MoTaskMaterialLog MoTaskMaterialLog);

    /**
     * 保存【请填写功能名称】
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 结果
     */
    public int save(MoTaskMaterialLog MoTaskMaterialLog);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(String taskId, int dataNo);
}
