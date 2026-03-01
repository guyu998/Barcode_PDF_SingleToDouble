package com.singletodouble.web.mapper.base;

import java.util.List;

import com.singletodouble.web.domain.base.MoSerial;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoSerialMapper {
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoSerial searchDataById(Long id);

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
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(Long id);

}
