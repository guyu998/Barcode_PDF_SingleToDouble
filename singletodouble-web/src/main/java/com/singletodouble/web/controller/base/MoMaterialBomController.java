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

import com.github.pagehelper.util.StringUtil;
import com.singletodouble.common.annotation.Log;
import com.singletodouble.common.core.controller.BaseController;
import com.singletodouble.common.core.domain.AjaxResult;
import com.singletodouble.common.enums.BusinessType;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoMaterialBom;
import com.singletodouble.web.service.base.MoMaterialBomService;
import com.singletodouble.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@RestController
@RequestMapping("/base/material/bom")
public class MoMaterialBomController extends BaseController {
    @Autowired
    private MoMaterialBomService moMaterialBomService;

    /**
     * 查询【请填写功能名称】列表
     */
//    @PreAuthorize("@ss.hasPermi('base:bom:list')")
    @GetMapping("/list")
    public TableDataInfo getList(MoMaterialBom moMaterialBom) {
        startPage();
        List<MoMaterialBom> list = moMaterialBomService.searchList(moMaterialBom);
        return getDataTable(list);
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
//    @PreAuthorize("@ss.hasPermi('base:bom:query')")
    @GetMapping(value = "/{makerMatNo}/{custMatNo}/{orderNo}")    
    public AjaxResult getInfo(@PathVariable("makerMatNo") String makerMatNo, @PathVariable("custMatNo") String custMatNo, @PathVariable("orderNo") Integer orderNo) {
    	if (StringUtil.isEmpty(makerMatNo) || StringUtil.isEmpty(custMatNo) || orderNo == 0) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "物料编码不能为空。");
    	}
        return success(moMaterialBomService.searchDataById(makerMatNo, custMatNo, orderNo));
    }

    /**
     * 新增【半成品】
     */
//    @PreAuthorize("@ss.hasPermi('base:bom:add')")
    @Log(title = "半成品管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult addData(@RequestBody MoMaterialBom moMaterialBom) {
    	if (StringUtil.isEmpty(moMaterialBom.getMakerMatNo()) || 
    		StringUtil.isEmpty(moMaterialBom.getCustMatNo()) ||
    		moMaterialBom.getOrderNo() == 0 ||
    		StringUtil.isEmpty(moMaterialBom.getPartName()) ||
    		StringUtil.isEmpty(moMaterialBom.getBackNo())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "半成品信息不能为空。");
    	}
    	
    	moMaterialBom.setCreateBy(getUsername());
    	moMaterialBom.setActionType(WebConstant.ACTION_TYPE_ADD);
    	int retId = moMaterialBomService.save(moMaterialBom);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1004) {
        		errMsg = "半成品编码不能重复。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "半成品信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 修改【半成品】
     */
//    @PreAuthorize("@ss.hasPermi('base:bom:edit')")
    @Log(title = "半成品管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public AjaxResult editData(@RequestBody MoMaterialBom moMaterialBom) {
    	if (StringUtil.isEmpty(moMaterialBom.getMakerMatNo()) || 
    		StringUtil.isEmpty(moMaterialBom.getCustMatNo()) ||
    		moMaterialBom.getOrderNo() == 0 ||
    		StringUtil.isEmpty(moMaterialBom.getPartName()) ||
    		StringUtil.isEmpty(moMaterialBom.getBackNo())) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "半成品信息不能为空。");
    	}
    	
    	moMaterialBom.setUpdateBy(getUsername());
    	moMaterialBom.setActionType(WebConstant.ACTION_TYPE_EDIT);
    	int retId = moMaterialBomService.save(moMaterialBom);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "半成品信息不存在。";
        	} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "半成品信息更新失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }

    /**
     * 删除【半成品】
     */
//    @PreAuthorize("@ss.hasPermi('base:bom:remove')")
    @Log(title = "半成品管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{makerMatNo}/{custMatNo}/{orderNo}")    
    public AjaxResult removeData(@PathVariable("makerMatNo") String makerMatNo, @PathVariable("custMatNo") String custMatNo, @PathVariable("orderNo") Integer orderNo) {
    	if (StringUtil.isEmpty(makerMatNo) || StringUtil.isEmpty(custMatNo) || orderNo == 0) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "半成品信息不能为空。");
    	}
    	
    	MoMaterialBom srchCond = new MoMaterialBom();
    	srchCond.setMakerMatNo(makerMatNo);
    	srchCond.setCustMatNo(custMatNo);
    	srchCond.setOrderNo(orderNo);
    	int retId = moMaterialBomService.deleteDataById(srchCond);
    	if (retId != 0) {
    		String errMsg = "";
    		if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "半成品信息不存在。";
    		} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "半成品信息删除失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
}
