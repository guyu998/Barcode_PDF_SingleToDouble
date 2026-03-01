package com.singletodouble.web.service.base.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.util.StringUtil;
import com.singletodouble.util.xls.ExcelUtil;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoCustomer;
import com.singletodouble.web.mapper.base.MoCustomerMapper;
import com.singletodouble.web.service.base.MoCustomerService;

/**
 * 【顾客】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoCustomerServiceImpl implements MoCustomerService {
    @Autowired
    private MoCustomerMapper moCustomerMapper;
    
    /**
     * 查询【顾客】
     * 
     * @param customerCode 【顾客】主键
     * @return 【顾客】
     */
    @Override
    public MoCustomer searchDataById(String customerCode) {
        return moCustomerMapper.searchDataById(customerCode);
    }

    /**
     * 查询【顾客】列表
     * 
     * @param moCustomer 【顾客】
     * @return 【顾客】
     */
    @Override
    public List<MoCustomer> searchList(MoCustomer moCustomer) {
        return moCustomerMapper.searchList(moCustomer);
    }

    /**
     * 保存【顾客】
     * 
     * @param moCustomer 【顾客】
     * @return 结果
     */
    @Override
    public int save(MoCustomer moCustomer) {
    	int rowCount = 0;
    	
    	// 检查
    	MoCustomer dbData = moCustomerMapper.searchDataById(moCustomer.getCustomerCode());
    	if (moCustomer.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		// 添加    		
        	if (dbData != null) {
    			return WebConstant.ERROR_ID_1004;
    		}

    		rowCount = moCustomerMapper.insertData(moCustomer);
    	} else {
    		// 修正
        	if (dbData == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = moCustomerMapper.updateData(moCustomer);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }

    /**
     * 批量删除【顾客】
     * 
     * @param customerCodes 需要删除的【顾客】主键
     * @return 结果
     */
    @Override
    public int deleteDataByIds(String[] customerCodes) {
        return moCustomerMapper.deleteDataByIds(customerCodes);
    }

    /**
     * 删除【顾客】信息
     * 
     * @param customerCode 【顾客】主键
     * @return 结果
     */
    @Override
    public int deleteDataById(String customerCode) {
    	MoCustomer dbData = searchDataById(customerCode);
    	if (dbData == null) {
    		return WebConstant.ERROR_ID_1003;
    	}  
    	
    	int rowCount = moCustomerMapper.deleteDataById(customerCode);
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }
    
    /**
     * 导入数据
     */
    @Override
    public int importFile(MultipartFile file) {
    	int rowCount = 0;
    	
    	InputStream is = null;
    	try {
        	// 读取excel并入库
    		is = file.getInputStream();
            ExcelUtil<MoCustomer> util = new ExcelUtil<MoCustomer>(MoCustomer.class);
            List<MoCustomer> dataList = util.importExcel(is, 0);
            // 数据检查
            for (MoCustomer moCustomer : dataList) {
            	if (StringUtil.isEmpty(moCustomer.getCustomerCode()) ||
            		StringUtil.isEmpty(moCustomer.getCustomerName())) {
            		return WebConstant.ERROR_ID_1006;
            	}
            }
            
            // 批量写入
            rowCount = moCustomerMapper.insertDataList(dataList);
    	} catch (Exception e) {
    		rowCount = 0;
    	} finally {
    		IOUtils.closeQuietly(is);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }
}
