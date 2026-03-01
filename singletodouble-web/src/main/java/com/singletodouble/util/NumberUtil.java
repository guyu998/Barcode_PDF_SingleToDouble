package com.singletodouble.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {
	public static String toString(int value, String format) {
		DecimalFormat dcFormat = new DecimalFormat(format);
		return dcFormat.format(value);
	}

	public static int convertNull2Zero(String value) {
		int retValue = 0 ;
		try {
			if (value != null) {
				retValue =Integer.valueOf(value);
			}
			return retValue;
		} catch (Exception e) {
			return 0;
		}	
	}
	
	public static float convertFloatNull2Zero(String value) {
		float retValue = 0.0f;
		try {
			if (value != null) {
				retValue = Float.parseFloat(value);
			}
		} catch (Exception e) {
		}
		return retValue;
	}
	
	public static double convertDoubleNull2Zero(String value) {
		double retValue = 0;
		try {
			if (value != null) {
				retValue = Double.parseDouble(value);
			}
		} catch (Exception e) {
		}
		return retValue;
	}
	
	public static String toString(double value, String format) {
		DecimalFormat dcFormat = new DecimalFormat(format);
		return dcFormat.format(value);
	}
	
	public static BigDecimal convertDecimal(String val) {
		BigDecimal ret = null;
		try {
			ret = new BigDecimal(val);
		} catch (Exception e) {
			ret = null;
		}
		return ret;
	}
	
	public static String trimZero4Decimal(String val) {
		String result = "";
		try {
			BigDecimal bigData = convertDecimal(val);
			if (bigData != null) {
				if (bigData.compareTo(new BigDecimal(0)) == 0) {
					result = "0";
				} else {
					result = bigData.stripTrailingZeros().toPlainString();	
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	public static long convert2Long(String value) {
		long retValue = 0L;
		try {
			if (value != null) {
				retValue = Long.parseLong(value);
			}			
		} catch (Exception e) {
			retValue = 0L;
		}
		return retValue;
	}
	
	/**
	 * 判断数据范围
	 * @param minValue
	 * @param maxValue
	 * @param minLimitValue
	 * @param maxLimitValue
	 * @return
	 */
	public static boolean isRanger(String minValue, String maxValue, String minLimitValue, String maxLimitValue) {
		boolean isValidy = true;
		BigDecimal minLimitDecimal = convertDecimal(minLimitValue);
		BigDecimal maxLimitDecimal = convertDecimal(maxLimitValue);
		
		// 最小值超阈值判定
		BigDecimal minDecimal = NumberUtil.convertDecimal(minValue);
		BigDecimal maxDecimal = NumberUtil.convertDecimal(minValue);
		
		if (minDecimal == null || maxDecimal == null) {
			isValidy = false;
		} else {
			if (minLimitDecimal != null && minDecimal.compareTo(minLimitDecimal) == -1) {
				isValidy = false;
			}
			if (maxLimitDecimal != null && maxDecimal.compareTo(maxLimitDecimal) == 1) {
				isValidy = false;
			}
		}
		return isValidy;
	}
	
	/**
	 * 归一化
	 * @param val
	 * @param min
	 * @param max
	 * @param format
	 * @return
	 */
	public static String normalization(String val, BigDecimal min, BigDecimal max, DecimalFormat decimalFormat) {
		BigDecimal value = NumberUtil.convertDecimal(val);
		value = value.subtract(min).divide(max.subtract(min), 6, RoundingMode.HALF_UP);
		if (value.compareTo(new BigDecimal(1)) == 1) {
			value = new BigDecimal(1);
		} else if (value.compareTo(new BigDecimal(0)) == -1) {
			value = new BigDecimal(0);
		}
		return decimalFormat.format(value);
	}
	
	/**
	 * 归一化(反推)
	 * @param val
	 * @param min
	 * @param max
	 * @param format
	 * @return
	 */
	public static String reverseNormalization(String val, BigDecimal min, BigDecimal max, DecimalFormat decimalFormat) {
		BigDecimal value = NumberUtil.convertDecimal(val);
		value = min.add(max.subtract(min).multiply(value));
		return decimalFormat.format(value);
	}
	
	public static void main(String[] args) {
		System.err.println(NumberUtil.toString(5, "00"));
		System.err.println(NumberUtil.toString(23, "00"));
	}
}
