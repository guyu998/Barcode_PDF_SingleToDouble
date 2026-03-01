package com.singletodouble.web.service.task.impl;

import java.util.List;

import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.task.MoTaskMaterialLog;
import com.singletodouble.web.mapper.task.MoTaskMaterialLogMapper;
import com.singletodouble.web.service.task.MoTaskMaterialLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoTaskMaterialLogServiceImpl implements MoTaskMaterialLogService {
    @Autowired
    private MoTaskMaterialLogMapper MoTaskMaterialLogMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public MoTaskMaterialLog searchDataById(String taskId, int dataNo) {
    	MoTaskMaterialLog srchCond = new MoTaskMaterialLog();
    	srchCond.setTaskId(taskId);
    	srchCond.setDataNo(dataNo);
        return MoTaskMaterialLogMapper.searchDataById(srchCond);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MoTaskMaterialLog> searchList(MoTaskMaterialLog moTaskMaterialLog) {
        return MoTaskMaterialLogMapper.searchList(moTaskMaterialLog);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoTaskMaterialLog 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int save(MoTaskMaterialLog moTaskMaterialLog) {
    	int rowCount = 0;
    	
    	// 检查
    	MoTaskMaterialLog dbData = searchDataById(moTaskMaterialLog.getTaskId(), moTaskMaterialLog.getDataNo());
    	if (moTaskMaterialLog.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		// 添加    		
        	if (dbData != null) {
    			return WebConstant.ERROR_ID_1004;
    		}

    		rowCount = MoTaskMaterialLogMapper.insertData(moTaskMaterialLog);
    	} else {
    		// 修正
        	if (dbData == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = MoTaskMaterialLogMapper.updateData(moTaskMaterialLog);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteDataById(String taskId, int dataNo) {
    	int rowCount = 0;
    	
    	MoTaskMaterialLog dbData = searchDataById(taskId, dataNo);
    	if (dbData == null) {
    		return WebConstant.ERROR_ID_1003;
    	}
    	
    	MoTaskMaterialLog srchCond = new MoTaskMaterialLog();
    	srchCond.setTaskId(taskId);
    	srchCond.setDataNo(dataNo);
    	rowCount = MoTaskMaterialLogMapper.deleteDataById(srchCond);

    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;   
    }
}
