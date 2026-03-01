package com.singletodouble.web.mapper.base;

import java.util.List;

import com.singletodouble.web.domain.base.MoMaterial;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoMaterialMapper {
    /**
     * 查询【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoMaterial searchDataById(MoMaterial moMaterial);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoMaterial 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoMaterial> searchList(MoMaterial moMaterial);

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoMaterial 【请填写功能名称】
     * @return 结果
     */
    public int insertData(MoMaterial moMaterial);

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoMaterial 【请填写功能名称】
     * @return 结果
     */
    public int insertDataList(List<MoMaterial> dataList);
    
    /**
     * 修改【请填写功能名称】
     * 
     * @param MoMaterial 【请填写功能名称】
     * @return 结果
     */
    public int updateData(MoMaterial moMaterial);

    /**
     * 删除【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(MoMaterial moMaterial);

}
