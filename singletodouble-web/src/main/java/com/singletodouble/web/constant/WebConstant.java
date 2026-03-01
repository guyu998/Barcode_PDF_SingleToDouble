package com.singletodouble.web.constant;

public class WebConstant {
	public static final String COMMA_STRING				= ",";
	public static final String LINE_STRING				= "-";
	public static final String DOT_STRING				= ".";
	public static final String FORWARD_SLASH_STRING		= "/";
	public static final String CHINESE_COMMA_STRING		= "，";
	
	// 更新类型
	public static final Integer ACTION_TYPE_ADD			= 1; // 添加
	public static final Integer ACTION_TYPE_EDIT		= 2; // 修正
	
	
	// 错误信息
	public static final Integer ERROR_ID_1001 			= 1001; // 必要参数为空
	public static final Integer ERROR_ID_1002 			= 1002; // 编号不能重复
	public static final Integer ERROR_ID_1003 			= 1003; // 数据不存在
	public static final Integer ERROR_ID_1004 			= 1004; // 名称重复
	public static final Integer ERROR_ID_1005 			= 1005; // 号码重复
	public static final Integer ERROR_ID_1006 			= 1006; // 上传文件不符合标准
	public static final Integer ERROR_ID_1007 			= 1007; // 外观检查项目正在使用
	public static final Integer ERROR_ID_2001 			= 2001; // 数据长度不合法
	
	public static final Integer ERROR_ID_9001 			= 9001; // 查询失败
	public static final Integer ERROR_ID_9002 			= 9002; // 更新失败
	public static final Integer ERROR_ID_9003 			= 9003; // 生成失败
	
	public static final Integer USE_STATUS_ENABLE 		= 1; // 使用
	public static final Integer USE_STATUS_DISABLE 		= 2; // 停用
	
	
    public static final String[] EXCEL_EXTENSION = { "xls", "xlsx" };
    public static final String[] PDF_EXTENSION = { "pdf"};
    
    public static final String FOLD_NAME_TOOL 	= "tool";
    public static final String FOLD_NAME_ZIP 	= "gswin64c";
    public static final String FILE_NAME_ZIP 	= "gswin64c.exe";
	
}
