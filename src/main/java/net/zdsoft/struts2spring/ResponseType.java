package net.zdsoft.struts2spring;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shenke date 2017/11/8下午2:24
 */
public enum ResponseType {

	REDIRECT,
	REDIRECT_ACTION,
	CHAIN,
	DISPATCHER,
	FREEMARKER,
	STREAM,
	JSON,
	EXCEL,
	CHINA_EXCEL,
	EXCEL_ERROR_DATA;

	private static Map<String, ResponseType> cache ;//= new HashMap<String, ResponseType>(16);
	static {
		cache = new HashMap<String, ResponseType>(16){{
			put(Struts2XMLConstant.RESULT_TYPE_REDIRECT, REDIRECT);
			put(Struts2XMLConstant.RESULT_TYPE_JSON, JSON);
			put(Struts2XMLConstant.RESULT_TYPE_FREEMARKER, FREEMARKER);
			put(Struts2XMLConstant.RESULT_TYPE_REDIRECT_ACTION, REDIRECT_ACTION);
			put(Struts2XMLConstant.RESULT_TYPE_CHAIN, CHAIN);
			put(Struts2XMLConstant.RESULT_TYPE_DISPATCHER, DISPATCHER);
			put(Struts2XMLConstant.RESULT_TYPE_STREAM, STREAM);
			put(Struts2XMLConstant.RESULT_TYPE_EXCEL, EXCEL);
			put(Struts2XMLConstant.RESULT_TYPE_EXCEL_ERROR_DATA, EXCEL_ERROR_DATA);
			put(Struts2XMLConstant.RESULT_TYPE_CHINA_EXCEL, CHINA_EXCEL);
		}};
	}

	public static ResponseType valueOfType(String responseType) {
		ResponseType type = cache.get(responseType);
		if ( type == null ) {
			throw new IllegalArgumentException("非法的返回类型 " + responseType);
		}
		return type;
	}
}
