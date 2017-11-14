package net.zdsoft.struts2spring.expression;

import net.zdsoft.eis.base.data.action.UserAction;
import net.zdsoft.passport.entity.Account;
import net.zdsoft.struts2spring.view.ExpressionHashModel;

import java.util.Date;

/**
 * @author shenke date 2017/11/12上午10:06
 */
public class ActionExpressionTest {

	public static void main(String[] args) {
		UserAction userAction = new UserAction();
		Account account = new Account();
		account.setBirthday(new Date());
		account.setId("sss");
		userAction.setAccount(account);

		//ExpressionHashModel expressionHashModel = n

		ActionExpressionContext context = new ActionExpressionContext(userAction);
		Object obj = ActionExpressionEvaluator.getValue("account", context);
		System.out.println(obj == null);
	}
}
