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
	
	public static final Integer USE_STATUS_ENABLE 		= 1; // 使用
	public static final Integer USE_STATUS_DISABLE 		= 2; // 停用
	
	public static final Integer APPEARANCE_TYPE_CHOOSE 	= 1; // 选择
	public static final Integer APPEARANCE_TYPE_INPUT 	= 2; // 输入
	public static final Integer APPEARANCE_TYPE_TEXT 	= 3; // 文字输入
	
	public static final Integer LABEL_TYPE_START_CHECK  = 1; // 初物
	public static final Integer LABEL_TYPE_END_CHECK  	= 2; // 终物
	
	public static final Integer CHECK_STATUS_DOING 		= 0; // 检查中
	public static final Integer CHECK_STATUS_OK 		= 1; // 检查OK
	public static final Integer CHECK_STATUS_NG 		= 2; // 检查NG
	public static final Integer CHECK_STATUS_NO 		= 3; // 无需检查
	
	public static final Integer BAR_CODE_YES			= 1; // 有条码
	public static final Integer BAR_CODE_NO				= 2; // 无条码
	
    public static final String[] EXCEL_EXTENSION = { "xls", "xlsx" };
    
    // 异常处置类别
    public static final Integer HANDLE_TYPE_ID_01		= 1; // 背番没有找到
    public static final Integer HANDLE_TYPE_ID_02		= 2; // 初物ID不匹配
    public static final Integer HANDLE_TYPE_ID_03		= 3; // 初物重扫
    public static final Integer HANDLE_TYPE_ID_04		= 4; // 强制当前ID为初物
    public static final Integer HANDLE_TYPE_ID_05		= 5; // 终物ID不匹配
    public static final Integer HANDLE_TYPE_ID_06		= 6; // 终物重扫
    public static final Integer HANDLE_TYPE_ID_07		= 7; // 强制当前ID为终物
    public static final Integer HANDLE_TYPE_ID_08		= 8; // 外观检查
    public static final Integer HANDLE_TYPE_ID_09		= 9; // 布标数量变更
    
    // 用户类别
	public static final Integer USER_TYPE_ABNORMAL 		= 1; // 异常处置人员
	public static final Integer USER_TYPE_CONFIRM 		= 2; // 检查确认人员
	
}
