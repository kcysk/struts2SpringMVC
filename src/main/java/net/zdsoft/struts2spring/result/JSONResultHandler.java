package net.zdsoft.struts2spring.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.zdsoft.struts2spring.Struts2JSONResultView;
import net.zdsoft.struts2spring.Struts2SpringMVCResultView;
import net.zdsoft.struts2spring.Struts2SpringMVCResponseHolder;
import net.zdsoft.struts2spring.invoke.ActionProxy;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author shenke date 2017/11/9下午6:33
 */
@Component
public class JSONResultHandler implements ResultHandler {

	@Override
	public void execute(Object action, Object root) {
		ExpressionParser parser = new SpelExpressionParser();
		EvaluationContext context = new StandardEvaluationContext();
		boolean proxy = action instanceof ActionProxy;
		context.setVariable("action", proxy ? ((ActionProxy) action).getAction() : action);

		try {
			HttpServletResponse response = Struts2SpringMVCResponseHolder.getResponse();
			if ( root instanceof Struts2JSONResultView) {
				Struts2JSONResultView view = (Struts2JSONResultView) root;
				Assert.notNull(view.getViewName());
				Object message = parser.parseExpression("#action." + ("action".equals(view.getViewName()) ? "message" : view.getViewName())).getValue(context);
				writeToResponse(message, view);
			} else if ( root instanceof Struts2SpringMVCResultView.View) {

			} else if ( root instanceof String ) {
				Object message = parser.parseExpression("#action." + root);
				OutputStream out = response.getOutputStream();
				out.write(JSON.toJSONString(message).getBytes());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}

	private void  writeToResponse(Object message, Struts2JSONResultView view) {
		if ( view == null ) {
			return ;
		}
		HttpServletResponse response = Struts2SpringMVCResponseHolder.getResponse();
		if ( view.isNoCache() ) {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Expires", "0");
			response.setHeader("Pragma", "No-cache");
		}
		if ( view.getContentType() != null ) {
			response.setContentType(view.getContentType() + ";charset=" + view.getEncoding());
		}
		Writer writer = null;
		try {
			writer = response.getWriter();
			writer.write(buildJSONString(message));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if ( writer != null ) {
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
				//do
			}
		}
	}

	private String buildJSONString(Object message) {
		return JSON.toJSONStringWithDateFormat(message, "yyyy-MM-dd HH:mm:ss", SerializerFeature.DisableCircularReferenceDetect);
	}
}
