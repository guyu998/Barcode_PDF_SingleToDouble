package com.singletodouble.web.mapper.task;

import java.util.List;

import com.singletodouble.web.domain.task.MoTaskMaterialLog;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoTaskMaterialLogMapper {
    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoTaskMaterialLog searchDataById(MoTaskMaterialLog moTaskMaterialLog);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoTaskMaterialLog> searchList(MoTaskMaterialLog moTaskMaterialLog);

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 结果
     */
    public int insertData(MoTaskMaterialLog moTaskMaterialLog);
    
    /**
     * 批量新增【请填写功能名称】
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 结果
     */
    public int insertDataList(List<MoTaskMaterialLog> dataList);

    /**
     * 修改【请填写功能名称】
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 结果
     */
    public int updateData(MoTaskMaterialLog moTaskMaterialLog);

    /**
     * 删除【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(MoTaskMaterialLog moTaskMaterialLog);

}
