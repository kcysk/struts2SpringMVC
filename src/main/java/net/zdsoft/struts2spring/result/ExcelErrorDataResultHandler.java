package net.zdsoft.struts2spring.result;

import org.springframework.stereotype.Component;

/**
 *
 * @author shenke date 2017/11/9下午6:34
 */
@Component()
public class ExcelErrorDataResultHandler implements ResultHandler {

	@Override
	public void execute(Object action, Object root) throws Exception {
		throw new RuntimeException("not support excel error data export");
	}
}
