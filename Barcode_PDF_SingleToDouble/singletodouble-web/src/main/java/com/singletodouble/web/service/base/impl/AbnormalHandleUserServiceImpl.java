package com.singletodouble.web.service.base.impl;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.singletodouble.util.xls.ExcelUtil;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.MoAbnormalHandleUser;
import com.singletodouble.web.mapper.base.MoAbnormalHandleUserMapper;
import com.singletodouble.web.service.base.AbnormalHandleUserService;
import com.github.pagehelper.util.StringUtil;

@Service
public class AbnormalHandleUserServiceImpl implements AbnormalHandleUserService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private MoAbnormalHandleUserMapper userMapper;
    
    private final static String SHEET_NAME = "异常处置人员表";
    private final static Object[][] XLS_VALUES_2_DB_VALUES_ARRAY = new Object[][] {
    	{
    		"异常处置员", Integer.valueOf(1)
    	},
    	{
    		"检查确认员", Integer.valueOf(2)
    	}  	
    };    
    
    /**
     * 用户列表
     */
	@Override
	public List<MoAbnormalHandleUser> searchList(MoAbnormalHandleUser moUser) {
		return userMapper.searchList(moUser);
	}

    /**
     * 查询 用户信息
     *
     * @param userQr  用户信息主键
     * @return  用户信息
     */
    @Override
    public MoAbnormalHandleUser searchDataById(String userQr) {
        return userMapper.searchUserById(userQr);
    }
    
    /**
     * 保存项目管理
     *
     * @param moProject 项目
     * @return 结果
     */
    @Override
    public int save(MoAbnormalHandleUser moUser) {
    	int rowCount = 0;
    	// 检查
    	if (moUser.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		// 添加
    		MoAbnormalHandleUser srchCondition = new MoAbnormalHandleUser();
    		srchCondition.setUserQr(moUser.getUserQr());
    		List<MoAbnormalHandleUser> dataList = userMapper.searchList(srchCondition);
    		if (!CollectionUtils.isEmpty(dataList)) {
    			return WebConstant.ERROR_ID_1004;
    		}

    		rowCount = userMapper.insertUser(moUser);
    	} else {
    		// 修正
    		MoAbnormalHandleUser dbUser = userMapper.searchUserById(moUser.getUserQr());
        	if (dbUser == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = userMapper.updateUser(moUser);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
	}
   
    @Override
    public int deleteById(String userQr) {
    	MoAbnormalHandleUser dbUser = searchDataById(userQr);
    	if (dbUser == null) {
    		return WebConstant.ERROR_ID_1003;
    	}
    	
    	int rowCount = userMapper.deleteUserById(userQr);
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }

    /**
     * 保存异常处置人员
     *
     * @param moProject 项目
     * @return 结果
     */
    @Override
    public int save(MultipartFile file, String userName) {
    	int rowCount = 0;
    	
    	InputStream is = null;
    	try {    		
    		// 单元格值和数据库值映射Map化
    		Map<Object, Object> xlsValues2DbValuesMap = new LinkedHashMap<Object, Object>();
    		for (Object[] datas : XLS_VALUES_2_DB_VALUES_ARRAY) {
    			xlsValues2DbValuesMap.put(datas[0], datas[1]);
    		}
    		
        	// 读取excel并入库
    		is = file.getInputStream();
            ExcelUtil<MoAbnormalHandleUser> util = new ExcelUtil<MoAbnormalHandleUser>(MoAbnormalHandleUser.class);
            List<MoAbnormalHandleUser> dataList = util.importExcel(SHEET_NAME, is, 0, null);
            
            // 数据检查
            for (MoAbnormalHandleUser moUser : dataList) {
            	if (StringUtil.isEmpty(moUser.getUserQr()) ||
            		StringUtil.isEmpty(moUser.getUserName()) ||
            		StringUtil.isEmpty(moUser.getUserType())) {
            		return WebConstant.ERROR_ID_1006;
            	}
            	
            	String userType = moUser.getUserType();
            	if (!StringUtil.isEmpty(userType)) {
            		userType = userType.replaceAll(WebConstant.CHINESE_COMMA_STRING, WebConstant.COMMA_STRING);
            		StringBuilder dataBuilder = new StringBuilder();
            		String[] tmpDatas = userType.split(WebConstant.COMMA_STRING);
            		for (String data : tmpDatas) {
            			if (xlsValues2DbValuesMap.containsKey(data)) {
            				dataBuilder.append(xlsValues2DbValuesMap.get(data));
            				dataBuilder.append(WebConstant.COMMA_STRING);
            			}
            		}
            		if (dataBuilder.length() > 0) {
            			dataBuilder = dataBuilder.deleteCharAt(dataBuilder.length() - 1);
            			moUser.setUserType(dataBuilder.toString());
            		}
            	}
            	moUser.setCreateBy(userName);
            	moUser.setUseStatus(1);
            }
            
            // 新增的入库
            if (!CollectionUtils.isEmpty(dataList)) {
            	rowCount = userMapper.insertUserList(dataList);	
            }
    	} catch (Exception e) {
    		rowCount = 0;
    		logger.error(String.format("上传数据失败：%s", e.getMessage()));
    	} finally {
    		IOUtils.closeQuietly(is);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
	}
}
