package net.zdsoft.struts2spring.expression;

import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 若遇到特殊通用参数，可以扩展
 * @author shenke date 2017/11/11下午12:03
 */
public class ActionExpressionContext extends StandardEvaluationContext {

	private Object action;

	public ActionExpressionContext(Object action) {
		super(action);
		this.action = action;
	}
}
