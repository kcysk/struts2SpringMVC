package net.zdsoft.struts2spring.result;

import net.zdsoft.struts2spring.Struts2JSONResultView;

/**
 * @author shenke date 2017/11/10上午9:10
 */
public interface ResultHandler {

	/**
	 * 针对struts2定义的特殊result类型需要特殊处理
	 * @param action Controller
	 * @param root 针对JSONResultHandler root是{@link Struts2JSONResultView}
	 *             针对ExcelResultHandler root是String Action中ExportUtils的name
	 */
	void execute(Object action, Object root ) throws Exception;
}
