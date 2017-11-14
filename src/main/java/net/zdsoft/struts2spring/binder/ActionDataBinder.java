package net.zdsoft.struts2spring.binder;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

import javax.servlet.ServletRequest;

/**
 * 绑定Action参数，将Action作为Model（haha）
 * @author shenke date 2017/11/14下午1:39
 */
public class ActionDataBinder extends ServletRequestDataBinder {

	public ActionDataBinder(Object target) {
		super(target);
	}

	public ActionDataBinder(Object target, String objectName) {
		super(target, objectName);
	}

	/**
	 * 重写方法，不处理文件
	 * @param request
	 */
	@Override
	public void bind(ServletRequest request) {
		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
		super.doBind(mpvs);
	}
}
