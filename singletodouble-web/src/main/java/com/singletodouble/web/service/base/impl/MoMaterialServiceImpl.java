package com.singletodouble.web.service.base.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.pagehelper.util.StringUtil;
import com.singletodouble.common.utils.StringUtils;
import com.singletodouble.util.DateTimeUtil;
import com.singletodouble.web.constant.WebConstant;
import com.singletodouble.web.domain.base.MoMaterial;
import com.singletodouble.web.domain.base.MoMaterialBom;
import com.singletodouble.web.mapper.base.MoMaterialBomMapper;
import com.singletodouble.web.mapper.base.MoMaterialMapper;
import com.singletodouble.web.service.base.MoMaterialService;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-08
 */
@Service
public class MoMaterialServiceImpl implements MoMaterialService {
    @Autowired
    private MoMaterialMapper moMaterialMapper;
    
    @Autowired
    private MoMaterialBomMapper moMaterialBomMapper;
    
    private static final String SHEET_NAME_MATERIAL = "详细";
    private static final String SHEET_NAME_BOM 		= "半成品BOM";
    
    private static final int MATERIAL_HEADER_COUNT 			= 2; // 列头有几行
    private static final int BOM_HEADER_COUNT 				= 2; // 列头有几行
    
    private static final int START_BOM_NAME_INDEX 			= 12; // Bom列头开始
    private static final int END_BOM_NAME_INDEX 			= 18; // Bom列头结束
    
    /**
     * 查询【请填写功能名称】
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public MoMaterial searchDataById(String makerMatNo, String custMatNo, String custColorCode) {
    	MoMaterial srchCond = new MoMaterial();
    	srchCond.setMakerMatNo(makerMatNo);
    	srchCond.setCustMatNo(custMatNo);
    	srchCond.setCustColorCode(custColorCode);
        return moMaterialMapper.searchDataById(srchCond);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param MoMaterial 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<MoMaterial> searchList(MoMaterial moMaterial) {
        return moMaterialMapper.searchList(moMaterial);
    }

    /**
     * 保存【】
     * 
     * @param MoMaterial 【】
     * @return 结果
     */
    @Override
    public int save(MoMaterial moMaterial) {
    	int rowCount = 0;
    	
    	// 检查
    	MoMaterial dbData = moMaterialMapper.searchDataById(moMaterial);
    	if (moMaterial.getActionType() == WebConstant.ACTION_TYPE_ADD) {
    		// 添加    		
        	if (dbData != null) {
    			return WebConstant.ERROR_ID_1004;
    		}

    		rowCount = moMaterialMapper.insertData(moMaterial);
    	} else {
    		// 修正
        	if (dbData == null) {
        		return WebConstant.ERROR_ID_1003;
        	}

    		rowCount = moMaterialMapper.updateData(moMaterial);
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param makerMatNo 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteDataById(String makerMatNo, String custMatNo, String custColorCode) {
    	int rowCount = 0;
    	
    	MoMaterial dbData = searchDataById(makerMatNo, custMatNo, custColorCode);
    	if (dbData == null) {
    		return WebConstant.ERROR_ID_1003;
    	}  
    	
    	MoMaterial moMaterial = new MoMaterial();
    	moMaterial.setMakerMatNo(makerMatNo);
    	moMaterial.setCustMatNo(custMatNo);
    	moMaterial.setCustColorCode(custColorCode);
    	rowCount = moMaterialMapper.deleteDataById(moMaterial);
    	
    	MoMaterialBom moMaterialBom = new MoMaterialBom();
    	moMaterialBom.setMakerMatNo(makerMatNo);
    	moMaterialBom.setCustMatNo(custMatNo);
    	moMaterialBom.setCustColorCode(custColorCode);
    	rowCount = moMaterialBomMapper.deleteDataById(moMaterialBom);

    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;   
    }
    
    /**
     * 导入数据
     */
    @Override
    @Transactional
    public int importFile(MultipartFile file) {
    	int rowCount = 0;
    	
    	InputStream is = null;
    	Workbook wb = null;
    	try {
        	// 读取excel并入库
    		is = file.getInputStream();
            wb = WorkbookFactory.create(is);
            
    		// 读取详细数据
            Sheet materialSheet = StringUtils.isNotEmpty(SHEET_NAME_MATERIAL) ? wb.getSheet(SHEET_NAME_MATERIAL) : wb.getSheetAt(0);
            List<MoMaterial> matDataList = importMaterialDataList(materialSheet);
            
            // 数据检查
            for (MoMaterial moMaterial : matDataList) {
            	if (StringUtil.isEmpty(moMaterial.getMakerMatNo()) ||
            		StringUtil.isEmpty(moMaterial.getCustMatNo())) {
            		return WebConstant.ERROR_ID_1006;
            	}
            }
            
            // 半成品BOM
            Sheet bomSheet = StringUtils.isNotEmpty(SHEET_NAME_BOM) ? wb.getSheet(SHEET_NAME_BOM) : wb.getSheetAt(1);
            List<MoMaterialBom> matBomDataList = importBomDataList(bomSheet);
            
            // 数据检查
            for (MoMaterialBom moMaterialBom : matBomDataList) {
            	if (StringUtil.isEmpty(moMaterialBom.getMakerMatNo()) ||
            		StringUtil.isEmpty(moMaterialBom.getCustMatNo()) ||
            		StringUtil.isEmpty(moMaterialBom.getPartName()) ||
            		StringUtil.isEmpty(moMaterialBom.getBackNo())) {
            		return WebConstant.ERROR_ID_1006;
            	}
            }

            // 批量写入
            rowCount = moMaterialMapper.insertDataList(matDataList);
            rowCount += moMaterialBomMapper.insertDataList(matBomDataList);
    	} catch (Exception e) {
    		rowCount = 0;
    	} finally {
    		try {
				IOUtils.close(wb);
				IOUtils.closeQuietly(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	return rowCount > 0 ? 0 : WebConstant.ERROR_ID_9002;
    }
    
    private List<MoMaterial> importMaterialDataList(Sheet sheet) {
    	List<MoMaterial> dataList = new ArrayList<MoMaterial>();
    	
    	int rows = sheet.getLastRowNum();
        if (rows > 0) {
            for (int i = MATERIAL_HEADER_COUNT; i <= rows; i++) {
                // 从第3行开始取数据,默认第一行字段名，第二行是表头.
                Row row = sheet.getRow(i);
                // 判断当前行是否是空行
                if (isRowEmpty(row)) {
                    continue;
                }
                // 颜色代码空，抛弃
                if (StringUtil.isEmpty(getCellValue(row, 42).toString())) {
                	continue;
                }
                
                MoMaterial moMaterial = new MoMaterial();
                moMaterial.setMakerBm(getCellValue(row, 0).toString());
                moMaterial.setMakerShortName(getCellValue(row, 1).toString());
                moMaterial.setMakerMatNo(getCellValue(row, 2).toString());
                moMaterial.setMakerBackNo(getCellValue(row, 3).toString());
                moMaterial.setMakerMatName(getCellValue(row, 4).toString());
                moMaterial.setMakerMatNameEn(getCellValue(row, 5).toString());
                moMaterial.setMakerColorCode(getCellValue(row, 6).toString());
                moMaterial.setMakerColor(getCellValue(row, 7).toString());
                moMaterial.setMakerSnp(getCellValue(row, 8).toString());
                moMaterial.setMakerCalUnit(getCellValue(row, 9).toString());
                moMaterial.setMakerBuyUnit(getCellValue(row, 10).toString());
                moMaterial.setMakerSpecModel(getCellValue(row, 11).toString());
                moMaterial.setMakerMatCatg(getCellValue(row, 12).toString());
                moMaterial.setMakerLeftRightFlag(getCellValue(row, 13).toString());
                moMaterial.setMakerImpProtectFlag(getCellValue(row, 14).toString());
                moMaterial.setMakerCarModel(getCellValue(row, 15).toString());
                moMaterial.setMakerCarPerQty(getCellValue(row, 16).toString());
                moMaterial.setMakerPlacePos(getCellValue(row, 17).toString());
                moMaterial.setMakerPostProc(getCellValue(row, 18).toString());
                moMaterial.setMakerPackBm(getCellValue(row, 19).toString());
                moMaterial.setMakerPackRate(getCellValue(row, 20).toString());
                moMaterial.setMakerPackGroup(getCellValue(row, 21).toString());
                moMaterial.setMakerTrayStdBoxQty(getCellValue(row, 22).toString());
                moMaterial.setMakerInitStockType(getCellValue(row, 23).toString());
                moMaterial.setMakerLabelColor(getCellValue(row, 24).toString());
                moMaterial.setMakerLabelTmpl(getCellValue(row, 25).toString());
                moMaterial.setMakerMatLabelTmpl(getCellValue(row, 26).toString());
                moMaterial.setMakerModTmpl(getCellValue(row, 27).toString());
                moMaterial.setMakerUseFlag(convert2Status(getCellValue(row, 28).toString()));
                moMaterial.setMakerLine(getCellValue(row, 29).toString());
                moMaterial.setMakerBackup2(getCellValue(row, 30).toString());
                moMaterial.setMakerBackup3(getCellValue(row, 31).toString());
                moMaterial.setStockDown(getCellValue(row, 32).toString());
                moMaterial.setStockUp(getCellValue(row, 33).toString());
                moMaterial.setHoldTimeMin(getCellValue(row, 34).toString());
                moMaterial.setHoldTimeMax(getCellValue(row, 35).toString());
                moMaterial.setCustBm(getCellValue(row, 36).toString());
                moMaterial.setCustShortName(getCellValue(row, 37).toString());
                moMaterial.setCustMatNo(getCellValue(row, 38).toString());
                moMaterial.setCustBackNo(getCellValue(row, 39).toString());
                moMaterial.setCustMatName(getCellValue(row, 40).toString());
                moMaterial.setCustMatNameEn(getCellValue(row, 41).toString());
                moMaterial.setCustColorCode(getCellValue(row, 42).toString());
                moMaterial.setCustColor(getCellValue(row, 43).toString());
                moMaterial.setCustSnp(getCellValue(row, 44).toString());
                moMaterial.setCustCalUnit(getCellValue(row, 45).toString());
                moMaterial.setCustBuyUnit(getCellValue(row, 46).toString());
                moMaterial.setCustSpecModel(getCellValue(row, 47).toString());
                moMaterial.setCustMatCatg(getCellValue(row, 48).toString());
                moMaterial.setCustLeftRightFlag(getCellValue(row, 49).toString());
                moMaterial.setCustImpProtectFlag(getCellValue(row, 50).toString());
                moMaterial.setCustCarModel(getCellValue(row, 51).toString());
                moMaterial.setCustCarPerQty(getCellValue(row, 52).toString());
                moMaterial.setCustPlacePos(getCellValue(row, 53).toString());
                moMaterial.setCustPostProc(getCellValue(row, 54).toString());
                moMaterial.setCustPackBm(getCellValue(row, 55).toString());
                moMaterial.setCustPackRate(getCellValue(row, 56).toString());
                moMaterial.setCustPackGroup(getCellValue(row, 57).toString());
                moMaterial.setCustLabelColor(getCellValue(row, 58).toString());
                moMaterial.setCustLabelTmpl(getCellValue(row, 59).toString());
                moMaterial.setCustMatLabelTmpl(getCellValue(row, 60).toString());
                moMaterial.setCustModTmpl(getCellValue(row, 61).toString());
                moMaterial.setCustUseFlag(convert2Status(getCellValue(row, 62).toString()));
                moMaterial.setCustBackup1(getCellValue(row, 63).toString());
                moMaterial.setCustBackup2(getCellValue(row, 64).toString());
                moMaterial.setCustBackup3(getCellValue(row, 65).toString());
                moMaterial.setPrepareLeadTime(convert2Date(getCellValue(row, 66).toString()));
                moMaterial.setSendLeadTime(convert2Date(getCellValue(row, 67).toString()));
                moMaterial.setMakerCal(getCellValue(row, 68).toString());
                moMaterial.setMakerLineBm(getCellValue(row, 69).toString());
                moMaterial.setMatOrder(getCellValue(row, 70).toString());
                moMaterial.setCreateBy(getCellValue(row, 71).toString());
                moMaterial.setCreateTime(convert2Date(getCellValue(row, 73).toString()));
                moMaterial.setUpdateBy(getCellValue(row, 74).toString());
                moMaterial.setUpdateTime(convert2Date(getCellValue(row, 76).toString()));

                dataList.add(moMaterial);
            }
        }
        
    	return dataList;
    }
    
    private List<MoMaterialBom> importBomDataList(Sheet sheet) {
    	List<MoMaterialBom> dataList = new ArrayList<MoMaterialBom>();
    	List<String> bomNameList = new ArrayList<String>();
    	
    	int rows = sheet.getLastRowNum();
        if (rows > 0) {
        	for (int j = START_BOM_NAME_INDEX; j <= END_BOM_NAME_INDEX; j++) {
        		bomNameList.add(sheet.getRow(1).getCell(j).getStringCellValue());	
        	}

            for (int i = BOM_HEADER_COUNT; i <= rows; i++) {
                // 从第3行开始取数据,默认第一行字段名，第二行是表头.
                Row row = sheet.getRow(i);
                // 判断当前行是否是空行
                if (isRowEmpty(row)) {
                    continue;
                }
                
                String makerMatNo = getCellValue(row, 2).toString();
                String custMatNo = getCellValue(row, 8).toString();
                String custColorCode = getCellValue(row, 10).toString();
                // 颜色代码空，抛弃
                if (StringUtil.isEmpty(custColorCode)) {
                	continue;
                }
                
                int index = 1;
                for (int j = START_BOM_NAME_INDEX; j <= END_BOM_NAME_INDEX; j++) {
                	String value = getCellValue(row, j).toString();
                	if (!StringUtil.isEmpty(value)) {
                        MoMaterialBom moMaterialBom = new MoMaterialBom();
                        moMaterialBom.setMakerMatNo(makerMatNo);
                        moMaterialBom.setCustMatNo(custMatNo);
                        moMaterialBom.setCustColorCode(custColorCode);
                        moMaterialBom.setOrderNo(index);
                        moMaterialBom.setPartName(bomNameList.get(j - START_BOM_NAME_INDEX));
                        moMaterialBom.setBackNo(value);
                        index++;
                        dataList.add(moMaterialBom);
                	}
                }
            }
        }
        
    	return dataList;
    }
    
    /**
     * 判断是否是空行
     * 
     * @param row 判断的行
     * @return
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
    
    private int convert2Status(String value) {
    	if (StringUtil.isEmpty(value)) {
    		return 0;
    	}
    	
    	return value.equals("使用中") ? 1 : 0;
    }
    
    private Date convert2Date(String value) {
    	if (StringUtil.isEmpty(value)) {
    		return null;
    	}
    	
    	return DateTimeUtil.convertString2Date(value, "yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 获取单元格值
     * 
     * @param row 获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    private Object getCellValue(Row row, int column) {
        if (row == null)
        {
            return row;
        }
        Object val = "";
        try
        {
            Cell cell = row.getCell(column);
            if (StringUtils.isNotNull(cell))
            {
                if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA)
                {
                    val = cell.getNumericCellValue();
                    if (DateUtil.isCellDateFormatted(cell))
                    {
                        val = DateUtil.getJavaDate((Double) val); // POI Excel 日期格式转换
                    }
                    else
                    {
                        if ((Double) val % 1 != 0)
                        {
                            val = new BigDecimal(val.toString());
                        }
                        else
                        {
                            val = new DecimalFormat("0").format(val);
                        }
                    }
                }
                else if (cell.getCellType() == CellType.STRING)
                {
                    val = cell.getStringCellValue();
                }
                else if (cell.getCellType() == CellType.BOOLEAN)
                {
                    val = cell.getBooleanCellValue();
                }
                else if (cell.getCellType() == CellType.ERROR)
                {
                    val = cell.getErrorCellValue();
                }

            }
        }
        catch (Exception e)
        {
            return val;
        }
        return val;
    }
}
