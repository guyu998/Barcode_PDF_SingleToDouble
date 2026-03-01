package com.singletodouble.web.controller.task;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.util.StringUtil;
import com.singletodouble.common.annotation.Log;
import com.singletodouble.common.core.controller.BaseController;
import com.singletodouble.common.core.domain.AjaxResult;
import com.singletodouble.common.enums.BusinessType;
import com.singletodouble.util.DateTimeUtil;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.task.MoTaskMaterial;
import com.singletodouble.web.service.task.MoTaskMaterialService;
import com.singletodouble.common.core.page.TableDataInfo;

/**
 * 【任务物料】Controller
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@RestController
@RequestMapping("/task/material")
public class MoTaskMaterialController extends BaseController {
    @Autowired
    private MoTaskMaterialService moTaskMaterialService;

    /**
     * 查询【任务物料】列表
     */
    @PreAuthorize("@ss.hasPermi('task:material:list')")
    @GetMapping("/list")
    public TableDataInfo getList(MoTaskMaterial moTaskMaterial) {
        startPage();
        List<MoTaskMaterial> list = moTaskMaterialService.searchList(moTaskMaterial);
        return getDataTable(list);
    }

    /**
     * 获取【任务物料】详细信息
     */
//    @PreAuthorize("@ss.hasPermi('task:material:query')")
    @GetMapping(value = "/{id}/{dataNo}")
    public AjaxResult getInfo(@PathVariable("id") String id, @PathVariable("dataNo") int dataNo) {
    	if (StringUtil.isEmpty(id) || dataNo == 0) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "任务物料ID不能为空。");
    	}       	
        return success(moTaskMaterialService.searchDataById(id, dataNo));
    }

    /**
     * 删除【任务物料】
     */
//    @PreAuthorize("@ss.hasPermi('task:material:remove')")
    @Log(title = "任务物料管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}/{dataNo}")
    public AjaxResult removeData(@PathVariable("id") String id, @PathVariable("dataNo") int dataNo) {
    	if (StringUtil.isEmpty(id) || dataNo == 0) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "任务物料ID不能为空。");
    	}

    	int retId = moTaskMaterialService.deleteDataById(id, dataNo);
    	if (retId != 0) {
    		String errMsg = "";
    		if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "任务物料信息不存在。";
    		} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "任务物料信息删除失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 删除【任务物料】
     */
//    @PreAuthorize("@ss.hasPermi('task:material:print')")
    @PostMapping("/supplementPrint")
    public void supplementPrint(HttpServletResponse response, @RequestBody List<MoTaskMaterial> dataList) {
        // 设置响应头
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.pdf\"", DateTimeUtil.getDateKey()));
        
		moTaskMaterialService.supplementPrint(dataList, response);
    }

}
