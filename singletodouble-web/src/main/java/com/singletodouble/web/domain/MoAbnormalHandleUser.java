package com.singletodouble.web.domain;

import com.singletodouble.common.annotation.Excel;
import com.singletodouble.common.annotation.Excel.ColumnType;
import com.singletodouble.common.core.domain.BaseEntity;

/**
 * 异常处置人员
 * @author sunyi
 *
 */
public class MoAbnormalHandleUser extends BaseEntity {
	
	private static final long serialVersionUID = 7025229673481916067L;
	
    @Excel(name = "二维码", cellType = ColumnType.STRING)
	private String userQr;
    
    @Excel(name = "姓名", cellType = ColumnType.STRING)
	private String userName;
    
    @Excel(name = "用户类型", cellType = ColumnType.STRING)
	private String userType;
    
	private int useStatus;
	
	public String getUserQr() {
		return userQr;
	}
	public void setUserQr(String userQr) {
		this.userQr = userQr;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

}
