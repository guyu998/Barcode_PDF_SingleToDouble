package com.singletodouble.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.singletodouble.common.annotation.Log;
import com.singletodouble.common.core.controller.BaseController;
import com.singletodouble.common.core.domain.AjaxResult;
import com.singletodouble.common.core.page.TableDataInfo;
import com.singletodouble.common.enums.BusinessType;
import com.singletodouble.common.utils.file.FileUploadUtils;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.MoAbnormalHandleUser;
import com.singletodouble.web.service.base.AbnormalHandleUserService;
import com.github.pagehelper.util.StringUtil;

/**
 * 异常处置用户管理Controller
 *
 * @author sunyi
 * @date 2024-09-06
 */
@RestController
@RequestMapping("/base/abnormaluser")
public class AbnormalHandleUserController extends BaseController {
	
    @Autowired
    private AbnormalHandleUserService userService;

    /**
     * 查询用户管理列表
     */
    @GetMapping("/list")
    public TableDataInfo getList(MoAbnormalHandleUser moUser) {        
        List<MoAbnormalHandleUser> list = userService.searchList(moUser);
        return getDataTable(list);        
    }
    
    /**
     * 获取用户详细信息
     */
    @GetMapping("/{userQr}")
    public AjaxResult getInfo(@PathVariable("userQr") String userQr) {
    	if (StringUtil.isEmpty(userQr)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "用户ID不能为空。");
    	}
        return AjaxResult.success(userService.searchDataById(userQr));
    }
    
    /**
     * 新增用户
     */
    @Log(title = "异常处置用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addUser(HttpServletRequest request, @RequestBody MoAbnormalHandleUser moUser) {
    	if (StringUtil.isEmpty(moUser.getUserQr()) || StringUtil.isEmpty(moUser.getUserName())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "异常处置用户信息不能为空。");
    	}
    	
    	moUser.setCreateBy(getUsername());
    	moUser.setActionType(WebConstant.ACTION_TYPE_ADD);
    	int retId = userService.save(moUser);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1004) {
        		errMsg = "异常处置用户二维码不能重复。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "异常处置用户信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 修改用户
     */
    @Log(title = "异常处置用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editUser(HttpServletRequest request, @RequestBody MoAbnormalHandleUser moUser) {
    	if (StringUtil.isEmpty(moUser.getUserQr()) || 
        	StringUtil.isEmpty(moUser.getUserName())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "异常处置用户信息不能为空。");
    	}
    	
    	moUser.setUpdateBy(getUsername());
    	moUser.setActionType(WebConstant.ACTION_TYPE_EDIT);
    	int retId = userService.save(moUser);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "异常处置用户信息不存在。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "异常处置用户更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 删除项目
     */
    @Log(title = "异常处置用户管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/delete/{userQr}")
    public AjaxResult removeUser(@PathVariable String userQr) {
    	if (StringUtil.isEmpty(userQr)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "异常处置用户二维码不能为空。");
    	}

    	int retId = userService.deleteById(userQr);
    	if (retId != 0) {
    		String errMsg = "";
    		if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "异常处置用户信息不存在。";
    		} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "异常处置人员删除失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 更改异常处置用户状态
     */
    @Log(title = "异常处置用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/updatestatus")
    public AjaxResult updateStatus(HttpServletRequest request, @RequestBody MoAbnormalHandleUser moUser) {
    	if (StringUtil.isEmpty(moUser.getUserQr()) || 
    		moUser.getUseStatus() > WebConstant.USE_STATUS_DISABLE || moUser.getUseStatus() < WebConstant.USE_STATUS_ENABLE) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "异常处置用户信息不能为空。");
    	}
    	
    	MoAbnormalHandleUser dbUser = userService.searchDataById(moUser.getUserQr());
    	if (dbUser == null) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1003, "异常处置用户信息不存在。");
    	}
    	
    	dbUser.setUpdateBy(getUsername());
    	dbUser.setUseStatus(moUser.getUseStatus());
    	int retId = userService.save(dbUser);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "异常处置用户更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 上传数据文件
     */
    @PostMapping("/upload")
    public AjaxResult saveFile(MultipartFile file) {        	
    	if (file == null) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "上传文件不能为空。");
    	}
    	
    	// 类型检查
    	try {
    		FileUploadUtils.assertAllowed(file, WebConstant.EXCEL_EXTENSION);
    	} catch (Exception e) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1006, "上传文件不符合标准。");
    	}
    	
    	int retId = userService.save(file, getUsername());
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "上传数据文件失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }    

}
