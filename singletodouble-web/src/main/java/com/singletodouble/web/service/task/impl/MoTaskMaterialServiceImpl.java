package com.singletodouble.web.service.task.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoMaterial;
import com.singletodouble.web.domain.task.MoTaskMaterial;
import com.singletodouble.web.domain.task.MoTaskMaterialLog;
import com.singletodouble.web.mapper.base.MoMaterialMapper;
import com.singletodouble.web.mapper.task.MoTaskMaterialLogMapper;
import com.singletodouble.web.mapper.task.MoTaskMaterialMapper;
import com.singletodouble.web.service.task.MoTaskMaterialService;
import com.singletodouble.web.service.task.MoTaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoTaskMaterialServiceImpl implements MoTaskMaterialService {
	private static final Logger log = LoggerFactory.getLogger(MoTaskMaterialServiceImpl.class);
	
    @Autowired
    private MoTaskMaterialMapper moTaskMaterialMapper;
    
    @Autowired
    private MoTaskMaterialLogMapper moTaskMaterialLogMapper;
    
    @Autowired
    private MoMaterialMapper moMaterialMapper;
    
    @Autowired
    private MoTaskService taskService;
    
    /**
     * 查询【请填写功能名称】
     * 
     * @param taskId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public MoTaskMaterial searchDataById(String taskId, int dataNo) {
    	MoTaskMaterial srchCond = new MoTaskMaterial();
    	srchCond.setTaskId(taskId);
    	srchCond.setDataNo(dataNo);
        return moTaskMaterialMapper.searchDataById(srchCond);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MoTaskMaterial> searchList(MoTaskMaterial moTaskMaterial) {
        return moTaskMaterialMapper.searchList(moTaskMaterial);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoTaskMaterial 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int save(MoTaskMaterial moTaskMaterial) {
    	int rowCount = 0;
    	
    	// 检查
    	MoTaskMaterial dbData = searchDataById(moTaskMaterial.getTaskId(), moTaskMaterial.getDataNo());
    	if (moTaskMaterial.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		// 添加    		
        	if (dbData != null) {
    			return WebConstant.ERROR_ID_1004;
    		}

    		rowCount = moTaskMaterialMapper.insertData(moTaskMaterial);
    	} else {
    		// 修正
        	if (dbData == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = moTaskMaterialMapper.updateData(moTaskMaterial);
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
    @Transactional
    public int deleteDataById(String taskId, int dataNo) {
    	int rowCount = 0;
    	
    	MoTaskMaterial dbData = searchDataById(taskId, dataNo);
    	if (dbData == null) {
    		return WebConstant.ERROR_ID_1003;
    	}  
    	
    	MoTaskMaterial delTaskMaterial = new MoTaskMaterial();
    	delTaskMaterial.setTaskId(taskId);
    	delTaskMaterial.setDataNo(dataNo);
    	rowCount = moTaskMaterialMapper.deleteDataById(delTaskMaterial);
    	
    	MoTaskMaterialLog delTaskMaterialLog = new MoTaskMaterialLog();
    	delTaskMaterialLog.setTaskId(taskId);
    	delTaskMaterialLog.setDataNo(dataNo);
    	rowCount += moTaskMaterialLogMapper.deleteDataById(delTaskMaterialLog);

    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;   
    }
    
    /**
     * 补充打印
     */
    @Override
    public void supplementPrint(List<MoTaskMaterial> dataList, HttpServletResponse response) {
        try {
        	// 根据提交的数据抽取对应打印数据
        	List<MoTaskMaterial> printList = moTaskMaterialMapper.searchListOnMultiNo(dataList);
        	
        	// 输出
        	taskService.writeData(printList, response.getOutputStream());
        } catch (Exception e) {
            log.error("导出Pdf异常{}", e.getMessage());
        }
    }
}
