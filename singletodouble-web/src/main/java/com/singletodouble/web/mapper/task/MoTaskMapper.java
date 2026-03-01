package com.singletodouble.web.mapper.task;

import java.util.List;

import com.singletodouble.web.domain.task.MoTask;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoTaskMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoTask searchDataById(String id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param moTask 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoTask> searchList(MoTask moTask);

    /**
     * 新增【请填写功能名称】
     * 
     * @param moTask 【请填写功能名称】
     * @return 结果
     */
    public int insertData(MoTask moTask);

    /**
     * 修改【请填写功能名称】
     * 
     * @param moTask 【请填写功能名称】
     * @return 结果
     */
    public int updateData(MoTask moTask);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(String id);
    
    /**
     * 查询指定日最大ID
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Integer searchMaxIdOnDate(String id);

}
