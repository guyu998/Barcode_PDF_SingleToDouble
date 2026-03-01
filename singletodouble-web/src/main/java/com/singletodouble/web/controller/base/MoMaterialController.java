package com.singletodouble.web.controller.base;

import java.util.List;
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
import com.singletodouble.web.domain.base.MoMaterial;
import com.singletodouble.web.service.base.MoMaterialService;
import com.singletodouble.common.core.page.TableDataInfo;

/**
 * 【物料】Controller
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@RestController
@RequestMapping("/base/material")
public class MoMaterialController extends BaseController {
    @Autowired
    private MoMaterialService moMaterialService;

    /**
     * 查询【物料】列表
     */
//    @PreAuthorize("@ss.hasPermi('base:material:list')")
    @GetMapping("/list")
    public TableDataInfo getList(MoMaterial moMaterial) {
        startPage();
        List<MoMaterial> list = moMaterialService.searchList(moMaterial);
        return getDataTable(list);
    }

    /**
     * 获取【物料】详细信息
     */
//    @PreAuthorize("@ss.hasPermi('base:material:query')")
    @GetMapping(value = "/{makerMatNo}/{custMatNo}/{custColorCode}")
    public AjaxResult getInfo(@PathVariable("makerMatNo") String makerMatNo, @PathVariable("custMatNo") String custMatNo, @PathVariable("custColorCode") String custColorCode) {
    	if (StringUtil.isEmpty(makerMatNo) || StringUtil.isEmpty(custMatNo) || StringUtil.isEmpty(custColorCode)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "物料编码不能为空。");
    	}       	
        return success(moMaterialService.searchDataById(makerMatNo, custMatNo, custColorCode));
    }

    /**
     * 新增【物料】
     */
//    @PreAuthorize("@ss.hasPermi('base:material:add')")
    @Log(title = "物料管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addData(@RequestBody MoMaterial moMaterial) {
    	if (StringUtil.isEmpty(moMaterial.getMakerMatNo()) || StringUtil.isEmpty(moMaterial.getCustMatNo())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "物料编码不能为空。");
    	}
    	
    	moMaterial.setCreateBy(getUsername());
    	moMaterial.setActionType(WebConstant.ACTION_TYPE_ADD);
    	int retId = moMaterialService.save(moMaterial);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1004) {
        		errMsg = "物料编码不能重复。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "物料信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 修改【物料】
     */
//    @PreAuthorize("@ss.hasPermi('base:material:edit')")
    @Log(title = "物料管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editData(@RequestBody MoMaterial moMaterial) {
    	if (StringUtil.isEmpty(moMaterial.getMakerMatNo()) || StringUtil.isEmpty(moMaterial.getCustMatNo())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "物料编码不能为空。");
    	}
    	
    	moMaterial.setUpdateBy(getUsername());
    	moMaterial.setActionType(WebConstant.ACTION_TYPE_EDIT);
    	int retId = moMaterialService.save(moMaterial);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "物料信息不存在。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "物料信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 删除【物料】
     */
//    @PreAuthorize("@ss.hasPermi('base:material:remove')")
    @Log(title = "物料管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{makerMatNo}/{custMatNo}")
    public AjaxResult removeData(@PathVariable("makerMatNo") String makerMatNo, @PathVariable("custMatNo") String custMatNo, @PathVariable("custColorCode") String custColorCode) {
    	if (StringUtil.isEmpty(makerMatNo) || StringUtil.isEmpty(custMatNo) || StringUtil.isEmpty(custColorCode)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "物料编码不能为空。");
    	}

    	int retId = moMaterialService.deleteDataById(makerMatNo, custMatNo, custColorCode);
    	if (retId != 0) {
    		String errMsg = "";
    		if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "物料信息不存在。";
    		} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "物料信息删除失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 上传数据文件
     */
//    @PreAuthorize("@ss.hasPermi('base:material:upload')")
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
    	
    	int retId = moMaterialService.importFile(file);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "上传物料数据文件失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
}
