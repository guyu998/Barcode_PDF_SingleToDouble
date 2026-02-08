package com.singletodouble.web.mapper.base;

import java.util.List;

import com.singletodouble.web.domain.MoAbnormalHandleUser;

public interface MoAbnormalHandleUserMapper {
	   /**
     * 查询用户列表
     *
     * @return 用户集合
     */
    public List<MoAbnormalHandleUser> searchList(MoAbnormalHandleUser param);
    
    /**
     * 查询用户信息
     *
     * @param id 用户信息主键
     * @return 用户信息
     */    
    public MoAbnormalHandleUser searchUserById(String userQr);
    
    /**
     * 添加用户信息
     *
     * @param param 项目信息
     * @return 成功条数
     */    
    public int insertUser(MoAbnormalHandleUser param);
    
    /**
     * 添加用户信息
     *
     * @param param 项目信息
     * @return 成功条数
     */    
    public int insertUserList(List<MoAbnormalHandleUser> dataList);
    
    /**
     * 修正用户信息
     *
     * @param param 项目信息
     * @return 成功条数
     */    
    public int updateUser(MoAbnormalHandleUser param);
    
    /**
     * 删除用户信息
     *
     * @param userQr 用户ID
     * @return 成功条数
     */    
    public int deleteUserById(String userQr);

}
