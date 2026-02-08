package com.singletodouble.util;

import javax.servlet.http.HttpServletRequest;

import com.singletodouble.common.utils.ip.IpUtils;

public class RequestUtil {

	public static String getIpAddr(HttpServletRequest request) {
		return IpUtils.getIpAddr(request);
	}
}
