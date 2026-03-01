package com.singletodouble.web.service.task.impl;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.github.pagehelper.util.StringUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.singletodouble.common.config.RuoYiConfig;
import com.singletodouble.common.constant.Constants;
import com.singletodouble.common.utils.DateUtils;
import com.singletodouble.util.DateTimeUtil;
import com.singletodouble.util.pdf.PdfUtil;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoMaterial;
import com.singletodouble.web.domain.base.MoMaterialBom;
import com.singletodouble.web.domain.task.MoTask;
import com.singletodouble.web.domain.task.MoTaskMaterial;
import com.singletodouble.web.domain.task.MoTaskMaterialLog;
import com.singletodouble.web.mapper.base.MoMaterialMapper;
import com.singletodouble.web.mapper.task.MoTaskMapper;
import com.singletodouble.web.mapper.task.MoTaskMaterialLogMapper;
import com.singletodouble.web.mapper.task.MoTaskMaterialMapper;
import com.singletodouble.web.service.base.MoSerialService;
import com.singletodouble.web.service.task.MoTaskService;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoTaskServiceImpl implements MoTaskService {
	private static final Logger log = LoggerFactory.getLogger(MoTaskServiceImpl.class);
	
    @Autowired
    private MoTaskMapper moTaskMapper;
    
    @Autowired
    private MoTaskMaterialMapper moTaskMaterialMapper;
    
    @Autowired
    private MoTaskMaterialLogMapper moTaskMaterialLogMapper;

    @Autowired
    private MoMaterialMapper moMaterialMapper;
    
    @Autowired
    private MoSerialService serialService;
    
	private PdfUtil pdfUtil = new PdfUtil();
    
    private static final int DATA_COUNT_IN_PAGE = 4;
    private static final int DATA_DISTANCE = 210;
    private static final int DATA_BACK_DISTANCE = 209;
    private static final int SERIAL_NUMBER_MAX = 9999;
    
    private static final int BACK_QR_LENGTH 		= 180; // 背面二维码长度
    private static final int MAKER_MAT_NO_LENGTH 	= 16; // TR品番长度
    private static final int CUST_MAT_NO_LENGTH 	= 18; // 客先背番长度
    private static final int CUST_SNP_LENGTH 		= 12; // 收容数长度
    private static final int SERIAL_NUMBER_LENGTH 	= 4; // 序列号长度
    
    private static Pattern PT_PATTERN = Pattern.compile("[（\\\\(](.*?)[\\\\)）]");
    
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public MoTask searchDataById(String id) {
        return moTaskMapper.searchDataById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param moTask 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MoTask> searchList(MoTask moTask) {
        return moTaskMapper.searchList(moTask);
    }
    
    @Override
    public int save(MoTask moTask) {
    	int rowCount = 0;
    	
    	// 检查
    	if (moTask.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		moTask.setId(getMaxIdOnDate());
    		
    		// 添加
    		rowCount = moTaskMapper.insertData(moTask);
    	} else {
    		// 修正
    		MoTask dbData = moTaskMapper.searchDataById(moTask.getId());
        	if (dbData == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = moTaskMapper.updateData(moTask);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDataById(String id) {
    	int rowCount = 0;
    	
    	MoTask dbData = searchDataById(id);
    	if (dbData == null) {
    		return WebConstant.ERROR_ID_1003;
    	}  
    	
    	rowCount = moTaskMapper.deleteDataById(id);
    	
    	MoTaskMaterial delTaskMaterial = new MoTaskMaterial();
    	delTaskMaterial.setTaskId(id);
    	rowCount += moTaskMaterialMapper.deleteDataById(delTaskMaterial);
    	
    	MoTaskMaterialLog delTaskMaterialLog = new MoTaskMaterialLog();
    	delTaskMaterialLog.setTaskId(id);
    	rowCount += moTaskMaterialLogMapper.deleteDataById(delTaskMaterialLog);
    	
    	// 删除文件
    	if (!StringUtil.isEmpty(dbData.getOutputFile())) {
    		StringBuilder outputBuilder = new StringBuilder();
    		outputBuilder.append(RuoYiConfig.getProfile());
    		outputBuilder.append(dbData.getOutputFile().replaceAll(Constants.RESOURCE_PREFIX, ""));
    		
    		new File(outputBuilder.toString()).delete();
    	}

    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;   
    }
    
    /**
     * 导入数据
     */
    @Override
    public int importFile(String customerCode, List<MultipartFile> files) {
    	int rowCount = 0;

		// 创建任务
		MoTask moTask = createTask(customerCode, files.size());
    	try {
        	// 读取
    		List<MoTaskMaterial> dataList = new ArrayList<MoTaskMaterial>();
    		List<MoTaskMaterialLog> logList = new ArrayList<MoTaskMaterialLog>();
    		for (MultipartFile file : files) {
    			importSinglePdf(moTask, file, dataList, logList);
    		}

    		// 重新排序(流水线/物料编码(顾客))
    		Collections.sort(dataList, 
    			Comparator
    				.comparing(MoTaskMaterial::getLineName, String.CASE_INSENSITIVE_ORDER)
    				.thenComparing(MoTaskMaterial::getCustMatNo, String.CASE_INSENSITIVE_ORDER)
    		);
    		
    		// 读取序列号
    		int startSerialNumber = serialService.getAndSetSerialNumber(dataList.size(), SERIAL_NUMBER_MAX);
    		
    		// 重新设定排序号好序列号
    		for (int i = 0; i < dataList.size(); i++) {
    			dataList.get(i).setDataNo(i + 1);
    			dataList.get(i).setSerialNumber((startSerialNumber + i + 1) % SERIAL_NUMBER_MAX);
    		}
 		
            // 批量写入
    		if (!CollectionUtils.isEmpty(dataList)) {
    			rowCount = moTaskMaterialMapper.insertDataList(dataList);	
    		}
    		if (!CollectionUtils.isEmpty(logList)) {
    			rowCount += moTaskMaterialLogMapper.insertDataList(logList);	
    		}
    		
            // 生成pdf
    		String outputFile = createPdfFile(String.format("%s.pdf", moTask.getId()), dataList);

            // 更新任务
    		if (moTask.getSuccessCount() == 0) {
    			moTask.setHandleStatus(4);	
    		} else if (moTask.getDataCount().intValue() == moTask.getSuccessCount().intValue()) {
    			moTask.setHandleStatus(2);
    		} else {
    			moTask.setHandleStatus(3);
    		}
            moTask.setEndHandleTime(new Date());
            moTask.setCostSec((int)DateTimeUtil.diffSecond(moTask.getEndHandleTime(), moTask.getStartHandleTime()));
            moTask.setOutputFile(outputFile);
    	} catch (Exception e) {
    		rowCount = 0;    		
            moTask.setHandleStatus(9);
            log.error(e.getMessage());
    	} finally {
    		try {
                moTaskMapper.updateData(moTask);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }
    
    private MoTask createTask(String customerCode, int fileCount) {
		// 创建
    	MoTask moTask = new MoTask();
    	moTask.setId(getMaxIdOnDate());
    	moTask.setCustBm(customerCode);
    	moTask.setUploadTime(DateUtils.getNowDate());
    	moTask.setFileCount(fileCount);
    	moTask.setPageCount(0);
    	moTask.setDataCount(0);
    	moTask.setSuccessCount(0);
    	moTask.setHandleStatus(1);
    	moTask.setStartHandleTime(new Date());
    	moTask.setPrintType(2);
    	moTaskMapper.insertData(moTask);
    	
    	return moTask;
    }
    
    private String getMaxIdOnDate() {
    	String curDate = DateUtils.getDate();
		Integer maxId = moTaskMapper.searchMaxIdOnDate(curDate);
		if (maxId == null) {
			maxId = 1;
		} else {
			maxId += 1;
		}
		
		return String.format("%s-%03d", curDate, maxId);
    }
    
    private void importSinglePdf(MoTask moTask, MultipartFile file, List<MoTaskMaterial> dataList, List<MoTaskMaterialLog> logList) {
		PdfDocument document = null;		
		try {
	    	// 读取文件
			document = pdfUtil.openDocument(file.getInputStream());

			// 匹配目标
			int pageSize = pdfUtil.getPageSize(document);
			int dataCount = 0, successCount = 0;
			for (int i = 0; i < pageSize; i++) {
				for (int dataIndex = 0; dataIndex < DATA_COUNT_IN_PAGE; dataIndex++) {
					MoTaskMaterial taskMaterial = matchTarget(document, i + 1, dataIndex + 1);
                    taskMaterial.setTaskId(moTask.getId());
					// 物料编码和颜色类别同时没有,代表这个表格是空表格
					if (StringUtil.isEmpty(taskMaterial.getCustMatNo()) && StringUtil.isEmpty(taskMaterial.getMakerMatNo()) && StringUtil.isEmpty(taskMaterial.getCustColorCode())) {
						continue;
					}
					
					// 总数累加
					dataCount++;
					
					// 空值检查
					StringBuilder errBuilder = new StringBuilder();
					if (StringUtil.isEmpty(taskMaterial.getCustMatNo())) {
						// 物料编码(顾客)空,报错
						errBuilder.append("物料编码(顾客)为空;");
					}
					if (StringUtil.isEmpty(taskMaterial.getMakerMatNo())) {
						// 物料编码(制造商)空,报错
						errBuilder.append("物料编码(制造商)为空;");
					}
					if (StringUtil.isEmpty(taskMaterial.getCustColorCode())) {
						// 颜色编码空,报错
						errBuilder.append("颜色编码为空;");
					}
					
					// 关键字去物料表检索
					MoMaterial srchCond = new MoMaterial();
					srchCond.setMakerMatNo(taskMaterial.getMakerMatNo());
					srchCond.setCustMatNo(taskMaterial.getCustMatNo());
					srchCond.setCustColorCode(taskMaterial.getCustColorCode());
					MoMaterial moMaterial = moMaterialMapper.searchDataById(srchCond);
					if (moMaterial == null) {
						errBuilder.append("根据【物料编码】和【颜色编码】无法在物料数据中检索到对应数据;");
					} else {
						taskMaterial.setBomList(moMaterial.getBomList());
					}

					// 错误信息汇总
					if (errBuilder.length() > 0) {
						errBuilder = errBuilder.deleteCharAt(errBuilder.length() - 1);
						MoTaskMaterialLog taskLog = new MoTaskMaterialLog();
						taskLog.setTaskId(moTask.getId());
						taskLog.setDataNo(logList.size() + 1);
						taskLog.setLog(String.format("文件(%s)的第%s页的第%s条数据校验失败：%s", file.getOriginalFilename(), i + 1, dataIndex + 1, errBuilder.toString()));
						
						logList.add(taskLog);
					} else {
						successCount++;
						dataList.add(taskMaterial);
					}
				}
			}
			
	    	moTask.setPageCount(moTask.getPageCount() + pageSize);
	    	moTask.setDataCount(moTask.getDataCount() + dataCount);
	    	moTask.setSuccessCount(moTask.getSuccessCount() + successCount);
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(document);
		}
    }
    
	private MoTaskMaterial matchTarget(PdfDocument document, int pageNo, int dataIndex) {
		MoTaskMaterial pdfData = new MoTaskMaterial();
		
		// 背番
		float startX = 20;
		float startY = 755 - (dataIndex - 1) * DATA_DISTANCE;
		float endX = 140;
		float endY = 776 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setBackNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// TR纳入日
		startX = 72;
		startY = 735 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 138;
		endY = 750 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setTrEntryDate(getText(document, pageNo, startX, startY, endX, endY));
		
		// SEQ
		startX = 56;
		startY = 720 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 140;
		endY = 735 - (dataIndex - 1) * DATA_DISTANCE;	
		pdfData.setSeqNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 订货号
		startX = 72;
		startY = 700 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 140;
		endY = 715 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setOrderCode(getText(document, pageNo, startX, startY, endX, endY));
		
		// 订单NO
		startX = 72;
		startY = 685 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 138;
		endY = 695 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setOrderNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 总箱数
		startX = 72;
		startY = 670 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 138;
		endY = 680 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setBoxCount(getText(document, pageNo, startX, startY, endX, endY));
		
		// 纳入指示数量
		startX = 72;
		startY = 660 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 138;
		endY = 670 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setEntryTargetBoxCount(getText(document, pageNo, startX, startY, endX, endY));
		
		// 发行日
		startX = 72;
		startY = 640 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 138;
		endY = 650 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setPublishDate(getText(document, pageNo, startX, startY, endX, endY));
		
		// 物料编码(顾客) 
		startX = 150;
		startY = 810 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 360;
		endY = 825 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustMatNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 物料编码(制造商)/TR品番 */
		startX = 175;
		startY = 792 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 360;
		endY = 804 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setMakerMatNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 品名
		startX = 175;
		startY = 782 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 360;
		endY = 792 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustMatName(getText(document, pageNo, startX, startY, endX, endY));
		
		// 物料简码
		startX = 150;
		startY = 742 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 245;
		endY = 775 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustBackNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 收容数
		startX = 150;
		startY = 705 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 245;
		endY = 725 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustSnp(getText(document, pageNo, startX, startY, endX, endY));
		
		// 二维码
		startX = 254;
		startY = 705 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 326;
		endY = 777 - (dataIndex - 1) * DATA_DISTANCE;		
		pdfData.setCustQrImage(pdfUtil.readImage(document, pageNo, startX, startY, endX, endY));
		
		// W/C
		startX = 330;
		startY = 755 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 430;
		endY = 765 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustWc(getText(document, pageNo, startX, startY, endX, endY));
		
		// 库位
		startX = 330;
		startY = 730 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 430;
		endY = 740 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustPlacePos(getText(document, pageNo, startX, startY, endX, endY));
		
		// 箱种
		startX = 330;
		startY = 705 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 430;
		endY = 715 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setCustBoxType(getText(document, pageNo, startX, startY, endX, endY));
		
		// 线别
		startX = 150;
		startY = 675 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 430;
		endY = 690 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setLineName(getText(document, pageNo, startX, startY, endX, endY));
		
		// 批次号
		startX = 500;
		startY = 815 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 585;
		endY = 825 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setBatchNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 顾客名
		startX = 445;
		startY = 794 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 530;
		endY = 815 - (dataIndex - 1) * DATA_DISTANCE;
		String custName = getText(document, pageNo, startX, startY, endX, endY);
		pdfData.setCustName(custName);
		String custColorCode = "";
		if (!StringUtil.isEmpty(custName)) {
			custName = custName.replaceAll("\r", "").replaceAll("\n", "");
		}
		int posIndex = custName.indexOf("(");
		if (posIndex > 0) {
	        Matcher matcher = PT_PATTERN.matcher(custName);
	        if (matcher.find()) {
	        	custColorCode = matcher.group(1); // group(1) 是括号内的捕获组
	        }
			pdfData.setCustColorCode(custColorCode);	
		} else {
			pdfData.setCustColorCode("");			
		}
		
		// KD No.
		startX = 468;
		startY = 773 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 510;
		endY = 790 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setKdNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 受入场所编号
		startX = 513;
		startY = 773 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 555;
		endY = 783 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setEntryPlaceNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 纳入指示日期
		startX = 445;
		startY = 753 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 510;
		endY = 763 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setEntryDate(getText(document, pageNo, startX, startY, endX, endY));
		
		// 便次
		startX = 520;
		startY = 753 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 533;
		endY = 763 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setTipNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 时间
		startX = 543;
		startY = 753 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 575;
		endY = 763 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setEntryTime(getText(document, pageNo, startX, startY, endX, endY));
		
		// 出货日
		startX = 445;
		startY = 733 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 510;
		endY = 743 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setOutDate(getText(document, pageNo, startX, startY, endX, endY));
		
		// 托盘No.
		startX = 550;
		startY = 706 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 575;
		endY = 716 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setTrayNo(getText(document, pageNo, startX, startY, endX, endY));
		
		// 客先使用栏
		startX = 445;
		startY = 645 - (dataIndex - 1) * DATA_DISTANCE;
		endX = 570;
		endY = 690 - (dataIndex - 1) * DATA_DISTANCE;
		pdfData.setMemo(getText(document, pageNo, startX, startY, endX, endY));
		
		return pdfData;
	}

	private String getText(PdfDocument document, int pageNo, float startX, float startY, float endX, float endY) {
		return pdfUtil.readText(document, pageNo, startX, startY, endX, endY);
	}
	
	private String createPdfFile(String fileName, List<MoTaskMaterial> dataList) throws IOException {
		// 输出文件
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder.append(RuoYiConfig.getProfile());
		outputBuilder.append(WebConstant.FORWARD_SLASH_STRING);
		outputBuilder.append("download");
		outputBuilder.append(WebConstant.FORWARD_SLASH_STRING);
		outputBuilder.append(fileName);
			
		// 检查目录是否存在，不存在则创建
		File file = new File(outputBuilder.toString());
		file.getParentFile().mkdirs();
		
		// 生成
		writeData(dataList, FileUtil.getBufferedOutputStream(file.getAbsolutePath()));
		
		// 压缩
		zipFile(file.getAbsolutePath());

        return Constants.RESOURCE_PREFIX + "/download/" + fileName;
	}
	
    /**
     * 生成pdf输出流
     * @param dataList
     */
	@Override
    public void writeData(List<MoTaskMaterial> dataList, OutputStream output) {
		PdfDocument outputDoc = null;
		try {
	    	// 根绝打印类型（现在默认穿透）重新按照页编排数据列表
	    	int pageSize = (int) Math.ceil((double) dataList.size() / DATA_COUNT_IN_PAGE);
	    	
	    	// 按照每页分组
	    	List<List<MoTaskMaterial>> pageDataList = new ArrayList<List<MoTaskMaterial>>();
	    	// 初始化
	    	for (int i = 0; i < pageSize; i++) {
	    		pageDataList.add(new ArrayList<MoTaskMaterial>());
	    	}
	    	for (int i = 0; i < dataList.size(); i++) {
	    		MoTaskMaterial taskMaterial = dataList.get(i);
	    		int remainder = (i + 1) % pageSize;
	    		int pageIndex = remainder == 0 ? pageSize : remainder;
	    		pageDataList.get(pageIndex - 1).add(taskMaterial);
	    	}
	    	
	        // 输出文档初始化
	    	Map<String, PdfFont> fontMap = new HashMap<String, PdfFont>();
	    	outputDoc = initOutputPdf(output, fontMap);
	    	
	    	// 加载模版,获取所有字段位置信息
	    	Map<Integer, PdfFormXObject> formXObjMap = new HashMap<Integer, PdfFormXObject>();
	    	Map<String, Rectangle> fieldPositionMap = loadTemplate(outputDoc, formXObjMap);

	        // 填充表单数据
	        for (int i = 0; i < pageDataList.size(); i++) {
	        	fillPageData(i + 1, pageDataList.get(i), fontMap, formXObjMap, fieldPositionMap, outputDoc);
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(outputDoc);
		}
    }
	
	private PdfDocument initOutputPdf(OutputStream output, Map<String, PdfFont> fontMap) throws IOException {
        PdfWriter writer = new PdfWriter(output);
        writer.setCompressionLevel(9);
        PdfDocument outputDoc = new PdfDocument(writer);

        // 创建两种共享字体
        {
        	PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        	fontMap.put(StandardFonts.HELVETICA, font);
            outputDoc.addFont(font);
        }
        {
        	PdfFont font = PdfFontFactory.createFont(
                    "STSong-Light",
                    "UniGB-UCS2-H",
                    PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        	fontMap.put(StandardFonts.TIMES_ROMAN, font);
            outputDoc.addFont(font);
        }
        
        return outputDoc;
	}
	
	private Map<String, Rectangle> loadTemplate(PdfDocument outputDoc, Map<Integer, PdfFormXObject> formXObjMap) throws Exception {
		Map<String, Rectangle> fieldPositions = new HashMap<String, Rectangle>();
		PdfDocument templateDoc = null;
		try {
	    	// 模版
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("templatePdf/materialTemplate.pdf");
	        templateDoc = new PdfDocument(new PdfReader(inputStream));   
	        int numberOfPages = templateDoc.getNumberOfPages();
	        if (numberOfPages < 2) {
	            throw new IOException("模板需要至少2页");
	        }
	        
	        // 提取模板中的字段位置信息
	        PdfAcroForm templateForm = PdfAcroForm.getAcroForm(templateDoc, false);
	        if (templateForm != null) {
	            Map<String, PdfFormField> templateFields = templateForm.getFormFields();
	            if (templateFields != null) {
	                for (Map.Entry<String, PdfFormField> entry : templateFields.entrySet()) {
	                    String fieldName = entry.getKey();
	                    PdfFormField field = entry.getValue();
	                    
	                    // 获取字段的位置信息
	                    List<com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation> widgets = field.getWidgets();
	                    if (widgets != null && !widgets.isEmpty()) {
	                        Rectangle rect = widgets.get(0).getRectangle().toRectangle();
	                        fieldPositions.put(fieldName, rect);
	                    }
	                }
	            }
	        }

	        // 创建第1页的XObject
	        formXObjMap.put(1, templateDoc.getPage(1).copyAsFormXObject(outputDoc));
	        
	        // 创建第2页的XObject
	        formXObjMap.put(2, templateDoc.getPage(2).copyAsFormXObject(outputDoc));
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
	        IOUtils.closeQuietly(templateDoc);
		}
        
        return fieldPositions;
	}
    
    @SuppressWarnings("deprecation")
	private void fillPageData(int pageNo, List<MoTaskMaterial> dataList, Map<String, PdfFont> fontMap, Map<Integer, PdfFormXObject> formXObjMap,
    		Map<String, Rectangle> fieldPositionMap, PdfDocument outputDoc) throws Exception {
        try {
        	for (int pageIndex = 1; pageIndex <= 2; pageIndex++) {
    	        // 创建页
    	        PdfPage newPage = outputDoc.addNewPage();
    	        PdfCanvas canvas = new PdfCanvas(newPage);
    	        
    	        // 添加 XObject 背景
    	        canvas.addXObject(formXObjMap.get(pageIndex), 0, 0);

    	        // 创建表单
    	        PdfAcroForm form = PdfAcroForm.getAcroForm(outputDoc, true);
    	        
    	        // 填数据
        		for (int i = 0; i < dataList.size(); i++) {
        			if (pageIndex == 1) {
        				fillPage1Data(pageNo, pageIndex, i + 1, dataList.get(i), fontMap, fieldPositionMap, outputDoc, form);
        			} else {
        				fillPage2Data(pageNo, pageIndex, i + 1, dataList.get(i), fontMap, fieldPositionMap, outputDoc, form);
        			}
        		}
    	        
    	        // ⚡ 立即扁平化当前页
    	        form.flattenFields();   
        	}
	    } catch (Exception e) {
	        throw new Exception("添加字段失败: " + e.getMessage());
	    }
    }
       
	@SuppressWarnings("resource")
	private void fillPage1Data(int pageNo, int pageIndex, int i, MoTaskMaterial moTaskMaterial, 
			Map<String, PdfFont> fontMap, Map<String, Rectangle> fieldPositionMap, 
			PdfDocument outputDoc, PdfAcroForm form) throws Exception {
		// 正面
		// 背番
		String name = String.format("F-No%s-%s", i, 7);
		String keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getBackNo(), keyName, 
				"", 18, ColorConstants.WHITE, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

		// TR纳入日
		name = String.format("F-No%s-%s", i, 8);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getTrEntryDate(), keyName, 
				"", 8, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// SEQ
		name = String.format("F-No%s-%s", i, 9);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getSeqNo(), keyName, 
				"", 7, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
        
		// 订货号
		name = String.format("F-No%s-%s", i, 10);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getOrderCode(), keyName, 
				"", 7, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// 订单NO
		name = String.format("F-No%s-%s", i, 11);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getOrderNo(), keyName, 
				"", 6, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 总箱数
		name = String.format("F-No%s-%s", i, 12);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getBoxCount(), keyName, 
				"", 9, null, PdfFormField.ALIGN_RIGHT, fontMap, fieldPositionMap));
		
		// 纳入指示数量
		name = String.format("F-No%s-%s", i, 13);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getEntryTargetBoxCount(), keyName, 
				"", 9, null, PdfFormField.ALIGN_RIGHT, fontMap, fieldPositionMap));

		// 发行日
		name = String.format("F-No%s-%s", i, 14);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getPublishDate(), keyName, 
				"", 9, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 物料编码(顾客)
		name = String.format("F-No%s-%s", i, 1);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustMatNo(), keyName, 
				"", 18, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// 物料编码(制造商)/TR品番 */
		name = String.format("F-No%s-%s", i, 3);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getMakerMatNo(), keyName, 
				"", 14, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 品名
		name = String.format("F-No%s-%s", i, 4);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustMatName(), keyName, 
				StandardFonts.TIMES_ROMAN, 8, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 物料简码
		name = String.format("F-No%s-%s", i, 15);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustBackNo(), keyName, 
				"", 22, ColorConstants.WHITE, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 收容数
		name = String.format("F-No%s-%s", i, 16);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustSnp(), keyName, 
				"", 18, null, PdfFormField.ALIGN_RIGHT, fontMap, fieldPositionMap));
		
		// 二维码
        ImageData imageData = ImageDataFactory.create(moTaskMaterial.getCustQrImage());
        Image img = new Image(imageData);
        // 设置固定位置
        img.setFixedPosition(2 * pageNo - 1, 252, 703 - (i - 1) * DATA_DISTANCE);
        img.setWidth(72);
        img.setHeight(72);        
        (new Document(form.getPdfDocument())).add(img);

        // W/C
		name = String.format("F-No%s-%s", i, 17);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustWc(), keyName, 
				"", 9, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 库位
		name = String.format("F-No%s-%s", i, 18);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustPlacePos(), keyName, 
				"", 9, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 箱种
		name = String.format("F-No%s-%s", i, 19);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustBoxType(), keyName, 
				"", 9, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 线别
		name = String.format("F-No%s-%s", i, 21);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getLineName(), keyName, 
				"", 9, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

		// 
		name = String.format("F-No%s-%s", i, 20);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustBackNo(), keyName, 
				"", 15, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// 批次号
		name = String.format("F-No%s-%s", i, 6);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getBatchNo(), keyName, 
				"", 10, null, PdfFormField.ALIGN_RIGHT, fontMap, fieldPositionMap));

		// 顾客名
		name = String.format("F-No%s-%s", i, 22);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getCustName(), keyName, 
				StandardFonts.TIMES_ROMAN, 8, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// KD No.
		name = String.format("F-No%s-%s", i, 23);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getKdNo(), keyName, 
				"", 12, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 受入场所编号
		name = String.format("F-No%s-%s", i, 24);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getEntryPlaceNo(), keyName, 
				"", 12, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// 纳入指示日期
		name = String.format("F-No%s-%s", i, 25);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getEntryDate(), keyName, 
				"", 12, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 便次
		name = String.format("F-No%s-%s", i, 26);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getTipNo(), keyName, 
				"", 12, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

		// 时间
		name = String.format("F-No%s-%s", i, 27);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getEntryTime(), keyName, 
				"", 12, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// 出货日
		name = String.format("F-No%s-%s", i, 28);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getOutDate(), keyName, 
				"", 12, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
		
		// 托盘No.
		name = String.format("F-No%s-%s", i, 29);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getTrayNo(), keyName, 
				"", 12, null, PdfFormField.ALIGN_RIGHT, fontMap, fieldPositionMap));

		// 客先使用栏
		name = String.format("F-No%s-%s", i, 30);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getMemo(), keyName, 
				"", 12, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));
    }
	
	@SuppressWarnings("resource")
	private void fillPage2Data(int pageNo, int pageIndex, int i, MoTaskMaterial moTaskMaterial, 
			Map<String, PdfFont> fontMap, Map<String, Rectangle> fieldPositionMap, 
			PdfDocument outputDoc, PdfAcroForm form) throws Exception {
		// 反面
        // 背番
		String name = String.format("B-No%s-%s", i, 1);
		String keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getBackNo(), keyName, 
				"", 26, ColorConstants.WHITE, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

        // 物料编码(制造商)/TR品番 */
		name = String.format("B-No%s-%s", i, 2);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getMakerMatNo(), keyName, 
				"", 16, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

		// 线别
		name = String.format("B-No%s-%s", i, 3);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, moTaskMaterial.getLineName(), keyName, 
				"", 14, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));
        
        // 二维码
		ImageData imageData = ImageDataFactory.create(createQrImgBytes(moTaskMaterial.getMakerMatNo(), moTaskMaterial.getCustMatNo(), 
        		moTaskMaterial.getCustSnp(), moTaskMaterial.getSerialNumber(), 70, 70));
        Image img = new Image(imageData);
        // 设置固定位置
        img.setFixedPosition(2 * pageNo, 22, 640 - (i - 1) * DATA_BACK_DISTANCE);
        img.setWidth(70);
        img.setHeight(70);        
        (new Document(form.getPdfDocument())).add(img);

		// 序列号
		name = String.format("B-No%s-%s", i, 4);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, String.format("No.%04d", moTaskMaterial.getSerialNumber()), keyName, 
				"", 14, null, PdfFormField.ALIGN_LEFT, fontMap, fieldPositionMap));

		// 区分
		name = String.format("B-No%s-%s", i, 5);
		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
		form.addField(createTextField(outputDoc, name, "半成品", keyName, 
				StandardFonts.TIMES_ROMAN, 16, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

        for (int j = 0; j < moTaskMaterial.getBomList().size(); j++) {
        	MoMaterialBom moMaterialBom = moTaskMaterial.getBomList().get(j);
        	
    		name = String.format("B-No%s-%s", i, 6 + j * 2);
    		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
    		form.addField(createTextField(outputDoc, name, moMaterialBom.getPartName(), keyName, 
    				StandardFonts.TIMES_ROMAN, 10, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));

    		name = String.format("B-No%s-%s", i, 7 + j * 2);
    		keyName = String.format("%s-%s", name, 2 * (pageNo - 1) + pageIndex);
    		form.addField(createTextField(outputDoc, name, moMaterialBom.getBackNo(), keyName, 
    				"", 10, null, PdfFormField.ALIGN_CENTER, fontMap, fieldPositionMap));
        } 
    }
	
	private PdfTextFormField createTextField(PdfDocument outputDoc, String name, String value, String keyName, 
		String fontName, float fontSize, Color fontColor, int alignment,
		Map<String, PdfFont> fontMap, Map<String, Rectangle> fieldPositions) {
		PdfTextFormField field = null;
        // 获取模板中的字段位置，如果不存在则使用默认位置
        Rectangle fieldPosition = fieldPositions.get(name);
        if (fieldPosition != null) {
            // 创建文本字段
            field = PdfFormField.createText(outputDoc, fieldPosition, keyName, value);

            // 设置字体
            if (fontMap.containsKey(fontName)) {
            	field.setFont(fontMap.get(fontName));
            } else {
            	field.setFont(fontMap.get(StandardFonts.HELVETICA));
            }
            
            // 设置字体大小
            field.setFontSize(fontSize);

            // 设置字体颜色
            if (fontColor != null) {
            	field.setColor(fontColor);	
            } else {
            	field.setColor(ColorConstants.BLACK);
            }
            
            // 设置字段的文本对齐方式
            field.setJustification(alignment);
        }
        
        return field;
	}
    
//    private void fillPageData(List<MoTaskMaterial> dataList, PdfDocument outputDoc) throws Exception {
//    	// 模版
//		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("templatePdf/materialTemplate.pdf");
//		
//        // 步骤1: 读取模板并进行修改
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        PdfReader templateReader = new PdfReader(inputStream);
//        PdfWriter templateWriter = new PdfWriter(baos);
////        templateWriter.setCompressionLevel(9);
//        PdfDocument templateDoc = new PdfDocument(templateReader, templateWriter);        
//
//        // 获取 AcroForm 表单
//        PdfAcroForm form = PdfAcroForm.getAcroForm(templateDoc, true);
//
//        // 填充字段
//        for (int i = 0; i < dataList.size(); i++) {
//        	fillData(i + 1, form, dataList.get(i));
//        }
//
//        // 打平表单：使填写内容不可编辑（关键！）
//        form.flattenFields();
//        
//        // 关闭模板文档以保存修改
//        int pageSize = templateDoc.getNumberOfPages();
//        templateDoc.close();
//
//        // 步骤2: 重新读取修改后的文档并复制页面
//        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
//        PdfReader modifiedReader = new PdfReader(bais);
//        PdfDocument modifiedDoc = new PdfDocument(modifiedReader);
//
//        // 复制页面到输出文档
//        modifiedDoc.copyPagesTo(1, pageSize, outputDoc);
//
//        // 关闭临时文档
//        modifiedDoc.close();
//    }
//    
//    @SuppressWarnings("resource")
//	private void fillData(int i, PdfAcroForm form, MoTaskMaterial moTaskMaterial) throws Exception {
//    	// 背番
//        PdfFormField field = form.getField(String.format("F-No%s-%s", i, 7));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getBackNo());
//        }
//
//		// TR纳入日
//        field = form.getField(String.format("F-No%s-%s", i, 8));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getTrEntryDate());
//        }
//
//		// SEQ
//        field = form.getField(String.format("F-No%s-%s", i, 9));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getSeqNo());
//        }
//        
//		// 订货号
//        field = form.getField(String.format("F-No%s-%s", i, 10));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getOrderCode());
//        }
//
//		// 订单NO
//        field = form.getField(String.format("F-No%s-%s", i, 11));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getOrderNo());
//        }
//		
//		// 总箱数
//        field = form.getField(String.format("F-No%s-%s", i, 12));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getBoxCount());
//        }
//		
//		// 纳入指示数量
//        field = form.getField(String.format("F-No%s-%s", i, 13));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getEntryTargetBoxCount());
//        }
//
//		// 发行日
//        field = form.getField(String.format("F-No%s-%s", i, 14));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getPublishDate());
//        }
//		
//		// 物料编码(顾客) 
//        field = form.getField(String.format("F-No%s-%s", i, 1));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustMatNo());
//        }
//		
//		// 物料编码(制造商)/TR品番 */
//        field = form.getField(String.format("F-No%s-%s", i, 3));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getMakerMatNo());
//        }
//		
//		// 品名
//        field = form.getField(String.format("F-No%s-%s", i, 4));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustMatName());
//        }
//		
//		// 物料简码
//        field = form.getField(String.format("F-No%s-%s", i, 15));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustBackNo());
//        }
//		
//		// 收容数
//        field = form.getField(String.format("F-No%s-%s", i, 16));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustSnp());
//        }
//		
//		// 二维码
//        ImageData imageData = ImageDataFactory.create(moTaskMaterial.getCustQrImage());
//        Image img = new Image(imageData);
//        // 设置固定位置
//        img.setFixedPosition(252, 703 - (i - 1) * DATA_DISTANCE);
//        img.setWidth(72);
//        img.setHeight(72);        
//        (new Document(form.getPdfDocument())).add(img);
//
//        // W/C
//        field = form.getField(String.format("F-No%s-%s", i, 17));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustWc());
//        }
//		
//		// 库位
//        field = form.getField(String.format("F-No%s-%s", i, 18));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustPlacePos());
//        }
//		
//		// 箱种
//        field = form.getField(String.format("F-No%s-%s", i, 19));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustBoxType());
//        }
//		
//		// 线别
//        field = form.getField(String.format("F-No%s-%s", i, 21));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getLineName());
//        }
//        field = form.getField(String.format("F-No%s-%s", i, 20));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustBackNo());
//        }        
//		
//		// 批次号
//        field = form.getField(String.format("F-No%s-%s", i, 6));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getBatchNo());
//        } 
//		
//		// 顾客名
//        field = form.getField(String.format("F-No%s-%s", i, 22));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getCustName());
//        } 
//		
//		// KD No.
//        field = form.getField(String.format("F-No%s-%s", i, 23));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getKdNo());
//        }
//		
//		// 受入场所编号
//        field = form.getField(String.format("F-No%s-%s", i, 24));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getEntryPlaceNo());
//        }
//		
//		// 纳入指示日期
//        field = form.getField(String.format("F-No%s-%s", i, 25));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getEntryDate());
//        }
//		
//		// 便次
//        field = form.getField(String.format("F-No%s-%s", i, 26));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getTipNo());
//        }
//		
//		// 时间
//        field = form.getField(String.format("F-No%s-%s", i, 27));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getEntryTime());
//        }
//		
//		// 出货日
//        field = form.getField(String.format("F-No%s-%s", i, 28));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getOutDate());
//        }
//		
//		// 托盘No.
//        field = form.getField(String.format("F-No%s-%s", i, 29));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getTrayNo());
//        }
//		
//		// 客先使用栏
//        field = form.getField(String.format("F-No%s-%s", i, 30));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getMemo());
//        }
//        
//        // 反面
//        // 背番
//        field = form.getField(String.format("B-No%s-%s", i, 1));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getBackNo());
//        }
//        
//        // 物料编码(制造商)/TR品番 */
//        field = form.getField(String.format("B-No%s-%s", i, 2));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getMakerMatNo());
//        }
//        
//		// 线别
//        field = form.getField(String.format("B-No%s-%s", i, 3));
//        if (field != null) {
//            field.setValue(moTaskMaterial.getLineName());
//        }
//        
//        // 二维码
//        imageData = ImageDataFactory.create(createQrImgBytes(moTaskMaterial.getMakerMatNo(), moTaskMaterial.getCustMatNo(), 
//        		moTaskMaterial.getCustSnp(), moTaskMaterial.getSerialNumber(), 60, 60));
//        img = new Image(imageData);
//        // 设置固定位置
//        img.setFixedPosition(2, 22, 650 - (i - 1) * DATA_BACK_DISTANCE);
//        img.setWidth(55);
//        img.setHeight(55);        
//        (new Document(form.getPdfDocument())).add(img);
//
//		// 序列号
//        field = form.getField(String.format("B-No%s-%s", i, 4));
//        if (field != null) {
//            field.setValue(String.format("No.%04d", moTaskMaterial.getSerialNumber()));
//        }
//        
//		// 区分
//        field = form.getField(String.format("B-No%s-%s", i, 5));
//        if (field != null) {
//            field.setValue("半成品");
//        }
//        
//        for (int j = 0; j < moTaskMaterial.getBomList().size(); j++) {
//        	MoMaterialBom moMaterialBom = moTaskMaterial.getBomList().get(j);
//        	
//            field = form.getField(String.format("B-No%s-%s", i, 6 + j * 2));
//            if (field != null) {
//                field.setValue(moMaterialBom.getPartName());
//            }
//        	
//            field = form.getField(String.format("B-No%s-%s", i, 7 + j * 2));
//            if (field != null) {
//                field.setValue(moMaterialBom.getBackNo());
//            }
//        } 
//    }
    
    private byte[] createQrImgBytes(String makerMatNo, String custMatNo, String custSnp, int serialNumber, int width, int height) throws Exception {
    	StringBuilder sb = new StringBuilder(BACK_QR_LENGTH);
    	// 使用char数组批量添加
    	char[] spaces = new char[BACK_QR_LENGTH];
    	Arrays.fill(spaces, ' ');
    	sb.append(spaces);
    	
    	// TR品番,从1位开始，共16位
        String tmpData = String.format("%-16s", makerMatNo); // 左对齐，总宽16，空格填充
        for (int i = 0; i < MAKER_MAT_NO_LENGTH; i++) {
            sb.setCharAt(i, tmpData.charAt(i));
        }
        
        // 连番号
        tmpData = String.format("%04d", serialNumber); // 右对齐，总宽4，0填充
        for (int i = 0; i < SERIAL_NUMBER_LENGTH; i++) {
            sb.setCharAt(i+ 16, tmpData.charAt(i));
        }
        
        // 收容数
        tmpData = String.format("%-12s", custSnp); // 左对齐，总宽12，空格填充
        for (int i = 0; i < CUST_SNP_LENGTH; i++) {
            sb.setCharAt(i+ 20, tmpData.charAt(i));
        }
        
        // 客先背番
        tmpData = String.format("%-18s", custMatNo); // 左对齐，总宽18，空格填充
        for (int i = 0; i < CUST_MAT_NO_LENGTH; i++) {
            sb.setCharAt(i+ 56, tmpData.charAt(i));
        }

        // 使用最小化的二维码尺寸，平衡可读性和文件大小
        int optimalSize = Math.min(Math.max(width, 100), 100); // 限制在50-100之间

        // 生成二维码矩阵，使用最小纠错级别以减少数据量
        com.google.zxing.MultiFormatWriter writer = new com.google.zxing.MultiFormatWriter();
        Map<com.google.zxing.EncodeHintType, Object> hints = new java.util.HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1); // 设置最小边距
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 关键：字符集

        BitMatrix bitMatrix = writer.encode(sb.toString(), BarcodeFormat.QR_CODE, optimalSize, optimalSize, hints);
        // 创建具有透明背景的BufferedImage
        BufferedImage bufferedImage = new BufferedImage(optimalSize, optimalSize, BufferedImage.TYPE_INT_ARGB);
        
        // 填充透明背景
        for (int x = 0; x < optimalSize; x++) {
            for (int y = 0; y < optimalSize; y++) {
                if (bitMatrix.get(x, y)) {
                    // 二维码黑色部分
                    bufferedImage.setRGB(x, y, 0xFF000000); // 不透明黑色
                } else {
                    // 透明背景
                    bufferedImage.setRGB(x, y, 0x00000000); // 完全透明
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        
        return baos.toByteArray();
    }
    
    private void zipFile(String pdfFile) {
    	try {
        	String tmpFile = String.format("%s/%s.pdf", new File(pdfFile).getParent(), UUID.randomUUID().toString());
        	
        	List<String> cmdArrayList = new ArrayList<String>();
        	
        	// 压缩指令
        	cmdArrayList.add(String.format("%s/%s/%s/%s", RuoYiConfig.getProfile(), WebConstant.FOLD_NAME_TOOL, WebConstant.FOLD_NAME_ZIP, WebConstant.FILE_NAME_ZIP));
        	cmdArrayList.add("-sDEVICE=pdfwrite");
        	cmdArrayList.add("-dPDFSETTINGS=/ebook");
        	cmdArrayList.add("-dNOPAUSE");
        	cmdArrayList.add("-dQUIET");
        	cmdArrayList.add("-dNOPAUSE");
        	cmdArrayList.add("-dBATCH");
        	cmdArrayList.add("-sDEVICE=pdfwrite");
        	cmdArrayList.add("-dEmbedAllFonts=true");
        	cmdArrayList.add("-dBufferSpace=500000000");
        	cmdArrayList.add("-dMaxBitmap=2000000000");
        	cmdArrayList.add("-dNumRenderingThreads=4");
        	cmdArrayList.add("-dSubsetFonts=true");
        	cmdArrayList.add("-dCompressFonts=true");
        	cmdArrayList.add("-dCompressFonts=true");
        	cmdArrayList.add("-dInterpolateControl=0");
        	cmdArrayList.add("-sOutputFile=" + tmpFile);
        	cmdArrayList.add(pdfFile);
        	
    		ProcessBuilder builder = new ProcessBuilder(cmdArrayList);
    		builder.directory(new File(String.format("%s/%s", RuoYiConfig.getProfile(), "download")));
    		builder.redirectErrorStream(true);
    		Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[exe输出] " + line);
                }
            }
            
            // 步骤5: 等待进程结束并检查退出码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("压缩文件执行失败，退出码: " + exitCode);
            }
            
            // 改名
            new File(pdfFile).delete();
            new File(tmpFile).renameTo(new File(pdfFile));
            
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
    @Override
    public void recreate(String taskId) {
		try {
	    	MoTask taskDb = moTaskMapper.searchDataById(taskId);
	    	taskDb.setStartHandleTime(new Date());
	    	taskDb.setHandleStatus(1);
	    	moTaskMapper.updateData(taskDb);
	    	
	    	List<MoTaskMaterial> printList = moTaskMaterialMapper.searchListOnTask(taskId);
	        // 生成pdf			
			String outputFile = createPdfFile(String.format("%s.pdf", taskId), printList);
			
	        // 更新任务
			taskDb.setEndHandleTime(new Date());
			taskDb.setCostSec((int)DateTimeUtil.diffSecond(taskDb.getEndHandleTime(), taskDb.getStartHandleTime()));
			taskDb.setHandleStatus(2);
			taskDb.setOutputFile(outputFile);
	    	moTaskMapper.updateData(taskDb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}