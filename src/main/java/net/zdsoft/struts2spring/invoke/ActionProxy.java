package net.zdsoft.struts2spring.invoke;

import net.zdsoft.struts2spring.Struts2SpringMVCResultView;

/**
 * @author shenke date 2017/11/10下午2:00
 */
public interface ActionProxy {

	Object getAction();

	String invoke();

	Struts2SpringMVCResultView getStruts2View();

	String getResultString();
}
