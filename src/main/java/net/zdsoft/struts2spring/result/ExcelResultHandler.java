package net.zdsoft.struts2spring.result;

import net.zdsoft.keel.util.URLUtils;
import net.zdsoft.leadin.dataimport.template.ExcelTemplateUtil;
import net.zdsoft.struts2spring.Struts2SpringMVCResponseHolder;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

/**
 * @author shenke date 2017/11/9下午6:33
 */
@Component
public class ExcelResultHandler implements ResultHandler {

	private Logger logger = LoggerFactory.getLogger(ExcelResultHandler.class);

	@Override
	public void execute(Object action, Object root) throws Exception {
		OutputStream out = null;
		try {
			ExpressionParser parser = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			context.setVariable("action", action);

			HttpServletResponse response = Struts2SpringMVCResponseHolder.getResponse();
			response.setContentType("application/x-msdownload");
			response.setHeader("Cache-Control", "");
			ExcelTemplateUtil excelTemplateUtil = (ExcelTemplateUtil) parser.parseExpression("#action." + (String)root).getValue(context);
			String fileName = excelTemplateUtil.getFileName();
			if (null == fileName) {
				fileName = "Template";
			}
			boolean isZip = excelTemplateUtil.isHasMoreFileData();
			fileName += isZip ? ".zip" : ".xls";
			out = isZip ? new ZipOutputStream(response.getOutputStream()):
					new BufferedOutputStream(response.getOutputStream());

			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLUtils.encode(fileName, "UTF-8"));

			excelTemplateUtil.writeDataFile(out);
		} finally {
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				logger.error("关闭流出错");
			}
		}
	}
}
