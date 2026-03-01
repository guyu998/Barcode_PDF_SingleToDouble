package com.singletodouble.web.service.task;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.singletodouble.web.domain.task.MoTaskMaterial;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoTaskMaterialService {
    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoTaskMaterial searchDataById(String taskId, int dataNo);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoTaskMaterial> searchList(MoTaskMaterial MoTaskMaterial);

    /**
     * 保存【请填写功能名称】
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 结果
     */
    public int save(MoTaskMaterial MoTaskMaterial);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(String taskId, int dataNo);
    
    /**
     * 补充打印
     * @param dataList
     */
    public void supplementPrint(List<MoTaskMaterial> dataList, HttpServletResponse response);
}
