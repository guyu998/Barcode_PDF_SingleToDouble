package com.singletodouble.web.controller.task;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.util.StringUtil;
import com.singletodouble.common.annotation.Log;
import com.singletodouble.common.core.controller.BaseController;
import com.singletodouble.common.core.domain.AjaxResult;
import com.singletodouble.common.enums.BusinessType;
import com.singletodouble.common.utils.file.FileUploadUtils;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.task.MoTask;
import com.singletodouble.web.domain.task.MoTaskMaterialLog;
import com.singletodouble.web.service.task.MoTaskMaterialLogService;
import com.singletodouble.web.service.task.MoTaskService;
import com.singletodouble.common.core.page.TableDataInfo;

/**
 * 【任务】Controller
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@RestController
@RequestMapping("/task")
public class MoTaskController extends BaseController {
    @Autowired
    private MoTaskService moTaskService;
    
    @Autowired
    private MoTaskMaterialLogService logService;

    /**
     * 查询【任务】列表
     */
    @PreAuthorize("@ss.hasPermi('task:list')")
    @GetMapping("/list")
    public TableDataInfo getList(MoTask moTask) {
        startPage();
        List<MoTask> list = moTaskService.searchList(moTask);
        return getDataTable(list);
    }

    /**
     * 获取【任务】详细信息
     */
//    @PreAuthorize("@ss.hasPermi('task:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
    	if (StringUtil.isEmpty(id)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "任务ID不能为空。");
    	}       	
        return success(moTaskService.searchDataById(id));
    }

    /**
     * 删除【任务】
     */
//    @PreAuthorize("@ss.hasPermi('task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete/{id}")
    public AjaxResult removeData(@PathVariable("id") String id) {
    	if (StringUtil.isEmpty(id)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "任务ID不能为空。");
    	}

    	int retId = moTaskService.deleteDataById(id);
    	if (retId != 0) {
    		String errMsg = "";
    		if (retId == WebConstant.ERROR_ID_1003) {
        		errMsg = "任务信息不存在。";
    		} else if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "任务信息删除失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 上传数据文件
     */
//    @PreAuthorize("@ss.hasPermi('task:upload')")
    @PostMapping("/upload")   
    public AjaxResult uploadFile(String customerCode, @RequestParam("file") List<MultipartFile> files) {	
    	if (files == null) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "上传文件不能为空。");
    	}
    	
    	// 类型检查
    	try {
    		for (MultipartFile file : files) {
    			FileUploadUtils.assertAllowed(file, WebConstant.PDF_EXTENSION);	
    		}
    	} catch (Exception e) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1006, "上传文件不符合标准。");
    	}
    	
    	int retId = moTaskService.importFile(customerCode, files);
    	if (retId != 0) {
    		String errMsg = "";
        	if (retId == WebConstant.ERROR_ID_9002) {
        		errMsg = "上传任务数据文件失败。";
        	}
        	return AjaxResult.error(retId, errMsg);
    	}
    	
        return AjaxResult.success();
    }
    
    /**
     * 查询【任务】列表
     */
//    @PreAuthorize("@ss.hasPermi('task:log:list')")
    @GetMapping(value = "/log/{id}")
    public AjaxResult getLogList(@PathVariable("id") String id) {
    	MoTaskMaterialLog logQuery = new MoTaskMaterialLog();
    	logQuery.setTaskId(id);
    	List<MoTaskMaterialLog> dataList = logService.searchList(logQuery);

        return AjaxResult.success(dataList);
    }
    

    /**
     * 【重新生成】pdf文件
     */
//    @PreAuthorize("@ss.hasPermi('task:query')")
    @PostMapping("/recreate")
    public AjaxResult reCreate(String id) {
    	if (StringUtil.isEmpty(id)) {
    		return AjaxResult.error(WebConstant.ERROR_ID_1001, "任务ID不能为空。");
    	}
    	
    	moTaskService.recreate(id);
    	
        return success();
    }
}
