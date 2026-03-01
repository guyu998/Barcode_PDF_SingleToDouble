package com.singletodouble.web.service.base;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.singletodouble.web.domain.base.MoCustomer;

/**
 * 【顾客】Service接口
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
public interface MoCustomerService {
    /**
     * 查询【顾客】
     * 
     * @param customerCode 【顾客】主键
     * @return 【顾客】
     */
    public MoCustomer searchDataById(String customerCode);

    /**
     * 查询【顾客】列表
     * 
     * @param moCustomer 【顾客】
     * @return 【顾客】集合
     */
    public List<MoCustomer> searchList(MoCustomer moCustomer);

    /**
     * 保存【顾客】
     * 
     * @param moCustomer 【顾客】
     * @return 结果
     */
    public int save(MoCustomer moCustomer);

    /**
     * 批量删除【顾客】
     * 
     * @param customerCodes 需要删除的【顾客】主键集合
     * @return 结果
     */
    public int deleteDataByIds(String[] customerCodes);

    /**
     * 删除【顾客】信息
     * 
     * @param customerCode 【顾客】主键
     * @return 结果
     */
    public int deleteDataById(String customerCode);
    
    /**
     * 导入数据
     * @param file
     * @return
     */
    public int importFile(MultipartFile file);
}
