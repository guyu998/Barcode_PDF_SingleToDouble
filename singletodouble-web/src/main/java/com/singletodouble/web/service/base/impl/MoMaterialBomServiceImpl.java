package com.singletodouble.web.service.base.impl;

import java.util.List;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoMaterialBom;
import com.singletodouble.web.mapper.base.MoMaterialBomMapper;
import com.singletodouble.web.service.base.MoMaterialBomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 【半成品】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoMaterialBomServiceImpl implements MoMaterialBomService {
    @Autowired
    private MoMaterialBomMapper moMaterialBomMapper;

    /**
     * 查询【半成品】
     * 
     * @param makerMatNo 【半成品】主键
     * @return 【半成品】
     */
    @Override
    public MoMaterialBom searchDataById(String makerMatNo, String custMatNo, Integer orderNo) {
    	MoMaterialBom srchCond = new MoMaterialBom();
    	srchCond.setMakerMatNo(makerMatNo);
    	srchCond.setCustMatNo(custMatNo);
    	srchCond.setOrderNo(orderNo);
        return moMaterialBomMapper.searchDataById(srchCond);
    }

    /**
     * 查询【半成品】列表
     * 
     * @param MoMaterialBom 【半成品】
     * @return 【半成品】
     */
    @Override
    public List<MoMaterialBom> searchList(MoMaterialBom moMaterialBom) {
        return moMaterialBomMapper.searchList(moMaterialBom);
    }

    /**
     * 保存【半成品】
     * 
     * @param MoMaterialBom 【半成品】
     * @return 结果
     */
    @Override
    public int save(MoMaterialBom moMaterialBom) {
    	int rowCount = 0;
    	
    	// 检查
    	MoMaterialBom dbData = moMaterialBomMapper.searchDataById(moMaterialBom);
    	if (moMaterialBom.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		// 添加    		
        	if (dbData != null) {
    			return WebConstant.ERROR_ID_1004;
    		}

    		rowCount = moMaterialBomMapper.insertData(moMaterialBom);
    	} else {
    		// 修正
        	if (dbData == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = moMaterialBomMapper.updateData(moMaterialBom);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }

    /**
     * 删除【半成品】信息
     * 
     * @param makerMatNo 【半成品】主键
     * @return 结果
     */
    @Override
    public int deleteDataById(MoMaterialBom moMaterialBom) {
        return moMaterialBomMapper.deleteDataById(moMaterialBom);
    }
}
