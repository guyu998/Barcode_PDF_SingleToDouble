package com.singletodouble.web.service.base.impl;

import java.util.List;
import com.singletodouble.web.domain.base.MoSerial;
import com.singletodouble.web.mapper.base.MoSerialMapper;
import com.singletodouble.web.service.base.MoSerialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoSerialServiceImpl implements MoSerialService {
    @Autowired
    private MoSerialMapper moSerialMapper;

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param moSerial 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MoSerial> searchList(MoSerial moSerial) {
        return moSerialMapper.searchList(moSerial);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param moSerial 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertData(MoSerial moSerial) {
        return moSerialMapper.insertData(moSerial);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param moSerial 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateData(MoSerial moSerial) {
        return moSerialMapper.updateData(moSerial);
    }
    
    @Override
    public synchronized int getAndSetSerialNumber(int dataSize, int maxSize) {
    	int retId = 0;
    	
    	List<MoSerial> serialList = searchList(new MoSerial());
    	if (!CollectionUtils.isEmpty(serialList)) {
    		MoSerial moSerial = serialList.get(0);
    		retId = moSerial.getSerialNumber();
    		
    		moSerial.setSerialNumber((moSerial.getSerialNumber() + dataSize) % maxSize);
    		updateData(moSerial);
    	} else {
    		MoSerial moSerial = new MoSerial();
    		moSerial.setSerialNumber(dataSize);
    		insertData(moSerial);
    	}
    	
    	return retId;
    }
}
