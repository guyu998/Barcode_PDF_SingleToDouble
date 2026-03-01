package com.singletodouble.web.controller.base;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.util.StringUtil;
import com.singletodouble.common.annotation.Log;
import com.singletodouble.common.core.controller.BaseController;
import com.singletodouble.common.core.domain.AjaxResult;
import com.singletodouble.common.enums.BusinessType;
import com.singletodouble.common.utils.file.FileUploadUtils;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoCustomer;
import com.singletodouble.web.service.base.MoCustomerService;
import com.singletodouble.common.core.page.TableDataInfo;

/**
 * 【顾客】Controller
 * 
 * @author sunyi
 * @date 2026-02-08
 */
@RestController
@RequestMapping("/base/customer")
public class MoCustomerController extends BaseController {
    @Autowired
    private MoCustomerService moCustomerService;

    /**
     * 查询【顾客】列表
     */
//    @PreAuthorize("@ss.hasPermi('base:customer:list')")
    @GetMapping("/list")
    public TableDataInfo getList(MoCustomer moCustomer) {
        startPage();
        List<MoCustomer> list = moCustomerService.searchList(moCustomer);
        return getDataTable(list);
    }

    /**
     * 获取【顾客】详细信息
     */
//    @PreAuthorize("@ss.hasPermi('base:customer:query')")
    @GetMapping(value = "/{customerCode}")
    public AjaxResult getInfo(@PathVariable("customerCode") String customerCode) {
    	if (StringUtil.isEmpty(customerCode)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "顾客编码不能为空。");
    	}    	
        return success(moCustomerService.searchDataById(customerCode));
    }

    /**
     * 新增【顾客】
     */
//    @PreAuthorize("@ss.hasPermi('base:customer:add')")
    @Log(title = "顾客管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addData(HttpServletRequest request, @RequestBody MoCustomer moCustomer) {
    	if (StringUtil.isEmpty(moCustomer.getCustomerCode()) || StringUtil.isEmpty(moCustomer.getCustomerName())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "顾客信息不能为空。");
    	}
    	
    	moCustomer.setCreateBy(getUsername());
    	moCustomer.setActionType(WebConstant.ACTION_TYPE_ADD);
    	int retId = moCustomerService.save(moCustomer);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1004) {
        		errMsg = "顾客编码不能重复。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "顾客信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 修改【顾客】
     */
//    @PreAuthorize("@ss.hasPermi('base:customer:edit')")
    @Log(title = "顾客管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editData(@RequestBody MoCustomer moCustomer) {
    	if (StringUtil.isEmpty(moCustomer.getCustomerCode()) || StringUtil.isEmpty(moCustomer.getCustomerName())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "顾客信息不能为空。");
    	}
    	
    	moCustomer.setUpdateBy(getUsername());
    	moCustomer.setActionType(WebConstant.ACTION_TYPE_EDIT);
    	int retId = moCustomerService.save(moCustomer);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "顾客信息不存在。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "顾客信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 删除【顾客】
     */
//    @PreAuthorize("@ss.hasPermi('base:customer:remove')")
    @Log(title = "顾客管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{customerCodes}")
    public AjaxResult removeData(@PathVariable String customerCode) {
    	if (StringUtil.isEmpty(customerCode)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "顾客编码不能为空。");
    	}

    	int retId = moCustomerService.deleteDataById(customerCode);
    	if (retId != 0) {
    		String errMsg = "";
    		if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "顾客信息不存在。";
    		} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "顾客信息删除失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 上传数据文件
     */
//    @PreAuthorize("@ss.hasPermi('base:customer:upload')")
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) {    
    	if (file == null) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "上传文件不能为空。");
    	}
    	
    	// 类型检查
    	try {
    		FileUploadUtils.assertAllowed(file, WebConstant.EXCEL_EXTENSION);
    	} catch (Exception e) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1006, "上传文件不符合标准。");
    	}
    	
    	int retId = moCustomerService.importFile(file);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "上传顾客数据文件失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
}
