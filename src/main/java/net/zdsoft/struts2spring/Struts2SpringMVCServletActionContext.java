package net.zdsoft.struts2spring;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shenke date 2017/11/9下午1:49
 */
public class Struts2SpringMVCServletActionContext {

	public static HttpServletRequest getRequest(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static HttpServletResponse getResponse() {
		return Struts2SpringMVCResponseHolder.getResponse();
	}
}
