package net.zdsoft.struts2spring.invoke;

import net.zdsoft.struts2spring.Struts2SpringMVCResultView;
import org.codehaus.groovy.runtime.ReflectionMethodInvoker;

/**
 * @author shenke date 2017/11/10下午2:10
 */
public class Struts2ActionProxy implements ActionProxy {

	private static Object[] args = new Object[0];

	private Object action;
	private Struts2SpringMVCResultView view;
	private String result;
	private boolean executed;

	public Struts2ActionProxy(Object action, Struts2SpringMVCResultView view) {
		this.action = action;
		this.view = view;
	}

	@Override
	public Object getAction() {
		return this.action;
	}

	@Override
	public Struts2SpringMVCResultView getStruts2View() {
		return this.view;
	}

	@Override
	public String invoke() {
		if( executed ) {
			throw new RuntimeException("action is executed");
		}
		this.result = (String) ReflectionMethodInvoker.invoke(getAction(), view.getMethodName(), args);
		return result;
	}

	@Override
	public String getResultString() {
		if ( executed ) {
			return this.result;
		} else {
			throw new RuntimeException("action not execute");
		}
	}
}
