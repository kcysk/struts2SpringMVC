package net.zdsoft.struts2spring.expression;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author shenke date 2017/11/11下午12:00
 */
public class ActionExpressionEvaluator {

	private static ExpressionParser expressionParser;

	static {
		expressionParser = new SpelExpressionParser();
	}

	public ActionExpressionEvaluator() {

	}

	public static Object getValue(String key, ActionExpressionContext expressionContext) {
		return expressionParser.parseExpression("#this." + key).getValue(expressionContext);
	}

	public static Object getSelf(ActionExpressionContext expressionContext) {
		return expressionParser.parseExpression("#this").getValue(expressionContext);
	}
}
