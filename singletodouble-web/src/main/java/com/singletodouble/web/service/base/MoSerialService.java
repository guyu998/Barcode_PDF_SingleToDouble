package com.singletodouble.web.service.base;

import java.util.List;

import com.singletodouble.web.domain.base.MoSerial;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoSerialService {

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param moSerial 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoSerial> searchList(MoSerial moSerial);

    /**
     * 新增【请填写功能名称】
     * 
     * @param moSerial 【请填写功能名称】
     * @return 结果
     */
    public int insertData(MoSerial moSerial);

    /**
     * 修改【请填写功能名称】
     * 
     * @param moSerial 【请填写功能名称】
     * @return 结果
     */
    public int updateData(MoSerial moSerial);
    
    /**
     * 获取当前序列号,同时设定新的序列号
     * @return
     */
    public int getAndSetSerialNumber(int dataSize, int maxSize);

}
