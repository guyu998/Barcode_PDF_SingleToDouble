package com.singletodouble.web.service.task;

import java.io.OutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.singletodouble.web.domain.task.MoTask;
import com.singletodouble.web.domain.task.MoTaskMaterial;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoTaskService {
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
     * 保存【请填写功能名称】
     * 
     * @param moTask 【请填写功能名称】
     * @return 结果
     */
    public int save(MoTask moTask);
    
    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(String id);
    
    /**
     * 导入数据
     * @param file
     * @return
     */
    public int importFile(String customerCode, List<MultipartFile> files);
    
    /**
     * 生成pdf输出流
     * @param dataList
     */
    public void writeData(List<MoTaskMaterial> dataList, OutputStream output);
    
    
    /**
     * 重新生成
     * @param file
     * @return
     */
    public void recreate(String taskId);
    
}
