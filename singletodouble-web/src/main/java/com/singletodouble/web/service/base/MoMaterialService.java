package com.singletodouble.web.service.base;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.singletodouble.web.domain.base.MoMaterial;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoMaterialService {
    /**
     * 查询【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public MoMaterial searchDataById(String makerMatNo, String custMatNo, String custColorCode);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoMaterial 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<MoMaterial> searchList(MoMaterial moMaterial);

    /**
     * 保存【】
     * 
     * @param MoMaterial 【】
     * @return 结果
     */
    public int save(MoMaterial moMaterial);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteDataById(String makerMatNo, String custMatNo, String custColorCode);
    
    /**
     * 导入数据
     * @param file
     * @return
     */
    public int importFile(MultipartFile file);
}
