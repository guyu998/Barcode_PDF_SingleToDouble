package com.singletodouble.web.service.base;

import java.util.List;

import com.singletodouble.web.domain.base.MoMaterialBom;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoMaterialBomService {
    /**
     * 查询【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoMaterialBom searchDataById(String makerMatNo, String custMatNo, Integer orderNo);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoMaterialBom 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoMaterialBom> searchList(MoMaterialBom moMaterialBom);

    /**
     * 保存【请填写功能名称】
     * 
     * @param MoMaterialBom 【请填写功能名称】
     * @return 结果
     */
    public int save(MoMaterialBom moMaterialBom);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(MoMaterialBom moMaterialBom);
}
