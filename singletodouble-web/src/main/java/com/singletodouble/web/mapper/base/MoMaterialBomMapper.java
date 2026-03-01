package com.singletodouble.web.mapper.base;

import java.util.List;

import com.singletodouble.web.domain.base.MoMaterialBom;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoMaterialBomMapper {
    /**
     * 查询【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoMaterialBom searchDataById(MoMaterialBom moMaterial);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoMaterialBom 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoMaterialBom> searchList(MoMaterialBom moMaterial);

    /**
     * 新增【请填写功能名称】
     * 
     * @param MoMaterialBom 【请填写功能名称】
     * @return 结果
     */
    public int insertData(MoMaterialBom moMaterial);
    
    /**
     * 新增【请填写功能名称】
     * 
     * @param moMakerCustomerDetail 【请填写功能名称】
     * @return 结果
     */
    public int insertDataList(List<MoMaterialBom> dataList);

    /**
     * 修改【请填写功能名称】
     * 
     * @param MoMaterialBom 【请填写功能名称】
     * @return 结果
     */
    public int updateData(MoMaterialBom moMaterial);

    /**
     * 删除【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(MoMaterialBom moMaterial);

}
