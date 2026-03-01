package com.singletodouble.web.mapper.task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.singletodouble.web.domain.task.MoTaskMaterial;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoTaskMaterialMapper {
    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoTaskMaterial searchDataById(MoTaskMaterial moTaskMaterial);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoTaskMaterial> searchList(MoTaskMaterial moTaskMaterial);

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 结果
     */
    public int insertData(MoTaskMaterial moTaskMaterial);
    
    /**
     * 批量新增【请填写功能名称】
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 结果
     */
    public int insertDataList(List<MoTaskMaterial> dataList);

    /**
     * 修改【请填写功能名称】
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 结果
     */
    public int updateData(MoTaskMaterial moTaskMaterial);

    /**
     * 删除【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(MoTaskMaterial moTaskMaterial);
    
    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public List<MoTaskMaterial> searchListOnMultiNo(@Param("dataList") List<MoTaskMaterial> dataList);

    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public List<MoTaskMaterial> searchListOnTask(String taskId);
}
