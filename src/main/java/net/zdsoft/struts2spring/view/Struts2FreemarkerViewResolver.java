package net.zdsoft.struts2spring.view;

import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * @author shenke date 2017/11/11下午1:28
 */
public class Struts2FreemarkerViewResolver extends FreeMarkerViewResolver {

	@Override
	protected Class requiredViewClass() {
		return Struts2FreemarkerView.class;
	}
}
