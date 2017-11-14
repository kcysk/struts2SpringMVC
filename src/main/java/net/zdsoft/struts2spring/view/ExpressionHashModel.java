package net.zdsoft.struts2spring.view;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import net.zdsoft.struts2spring.expression.ActionExpressionContext;
import net.zdsoft.struts2spring.expression.ActionExpressionEvaluator;
import net.zdsoft.struts2spring.invoke.ActionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 继承SimpleHash 为了使用Spring EL获取Action数据栈的数据，
 * 防止Freemarker重新生成新的dataModelHash，这样我们的自定义的Spring表达式就不可用了
 * @author shenke date 2017/11/11上午9:37
 */
public class ExpressionHashModel extends SimpleHash implements TemplateModel {

	private Logger logger = LoggerFactory.getLogger(ExpressionHashModel.class);
	public static final String MODEL_ACTION_PROXY_ATTRIBUTE_NAME = "actionProxy";

	private HttpServletRequest request;
	private ActionProxy actionProxy;
	//modelMap in ModelAndView
	private Map<String, Object> modelMap;
	private ActionExpressionContext actionExpressionContext;
	private Map<String, TemplateModel> mapCache = new ConcurrentHashMap<String, TemplateModel>(16);

	public ExpressionHashModel(HttpServletRequest request, Map<String, Object> modelMap, ObjectWrapper wrapper) {
		super(wrapper);
		this.actionProxy = (ActionProxy) modelMap.get(MODEL_ACTION_PROXY_ATTRIBUTE_NAME);
		Assert.notNull(actionProxy);
		this.request = request;
		this.modelMap = modelMap;
		this.actionExpressionContext = new ActionExpressionContext(this.actionProxy.getAction());
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException {
		TemplateModel templateModel = super.get(key);
		//利用Spring EL从Action中查找数据包括Get方法
		if ( templateModel != null ) {
			return templateModel;
		}
		//FIXME Global Variables 避免Spring EL 抛出异常造成性能下降
		if ( "appsetting".equals(key)  ) {
			return null;
		}
		Object obj = this.modelMap.get(key);
		if ( obj != null ) {
			return super.wrap(obj);
		}
		try {
			if ( !"action".equals(key)) {
				obj = ActionExpressionEvaluator.getValue(key, actionExpressionContext);
			} else {
				obj = ActionExpressionEvaluator.getSelf(actionExpressionContext);
			}
		} catch (Exception e) {
			logger.error("Spring el error",e);
			return null;
		}
		if ( obj instanceof Map ) {
			TemplateModel model = mapCache.get(key);
			if ( model == null ) {
				model = HashMapWrapper.FACTORY.create(obj, super.getObjectWrapper());
				mapCache.put(key, model);
			}
			return model;
		}
		templateModel = super.wrap(obj);
		return templateModel;
	}

	ActionProxy getActionProxy() {
		return this.actionProxy;
	}

}
