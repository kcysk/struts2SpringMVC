package net.zdsoft.struts2spring.view;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.zdsoft.struts2spring.ResponseType;
import net.zdsoft.struts2spring.invoke.ActionProxy;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 重写Freemarker渲染逻辑，解决Struts2视图渲染数据栈的问题
 * 可以避免利用反射构建
 * @author shenke date 2017/11/10上午11:47
 */
public class Struts2FreemarkerView extends FreeMarkerView {

	@Override
	protected void doRender(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		super.doRender(model, request, response);
	}

	@Override
	protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		SimpleHash simpleHash = super.buildTemplateModel(model, request, response);

		ExpressionHashModel expressionHashModel = new ExpressionHashModel(request, model, super.getObjectWrapper());
		try {
			expressionHashModel.put("JspTaglibs", simpleHash.get("JspTaglibs"));
			expressionHashModel.put("Application", simpleHash.get("Application"));
			expressionHashModel.put("Session", simpleHash.get("Session"));
			expressionHashModel.put("Request", simpleHash.get("Request"));
			expressionHashModel.put("RequestParameters", simpleHash.get("RequestParameters"));
		} catch (TemplateModelException e) {
			throw new RuntimeException(e);
		}
		return expressionHashModel;
	}

	@Override
	protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response) throws IOException, TemplateException {
		TemplateModel templateModel = model.get(ExpressionHashModel.MODEL_ACTION_PROXY_ATTRIBUTE_NAME);
		if ( templateModel instanceof ExpressionHashModel) {
			ActionProxy actionProxy = ((ExpressionHashModel)templateModel).getActionProxy();
			if ( ResponseType.CHINA_EXCEL.equals(actionProxy.getStruts2View().getView(actionProxy.getResultString()).getViewType())) {
				String contentType = getContentType();
				contentType = contentType == null ? "text/html" : contentType;
				response.setContentType(contentType + "; charset=GBK");
			}
		}
		super.processTemplate(template, model, response);
	}
}
