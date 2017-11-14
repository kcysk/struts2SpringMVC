package net.zdsoft.struts2spring;

import javax.servlet.http.HttpServletResponse;

/**
 * @author shenke date 2017/11/9下午1:51
 */
public abstract class Struts2SpringMVCResponseHolder {
	private static final ThreadLocal<HttpServletResponse> RESPONSE_THREAD_LOCAL = new ThreadLocal<HttpServletResponse>();

	public static HttpServletResponse getResponse() {
		return RESPONSE_THREAD_LOCAL.get();
	}

	public static void resetResponse() {
		RESPONSE_THREAD_LOCAL.remove();
	}

	static void bindResponse(HttpServletResponse response) {
		RESPONSE_THREAD_LOCAL.set(response);
	}
}
