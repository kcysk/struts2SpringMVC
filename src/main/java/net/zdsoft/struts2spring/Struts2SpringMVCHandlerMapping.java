package net.zdsoft.struts2spring;

import net.zdsoft.struts2spring.invoke.ActionProxy;
import net.zdsoft.struts2spring.invoke.Struts2ActionProxy;
import org.springframework.beans.BeansException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shenke date 2017/11/8上午11:06
 */
@Component
public class Struts2SpringMVCHandlerMapping extends AbstractUrlHandlerMapping {


	private Map<String, Object> handlerMap = new HashMap<String, Object>(64);
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	private Map<String, Struts2SpringMVCResultView> handlerCache = new ConcurrentHashMap<String, Struts2SpringMVCResultView>(16);


	public void setMappings(Properties properties) {
		CollectionUtils.mergePropertiesIntoMap(properties, this.handlerMap);
	}

	@Override
	protected void initApplicationContext() throws BeansException {
		super.initApplicationContext();
		//FIXME 需读取全部的XML配置文件
		//registerHandlers(this.handlerMap);
		ClassPathResource classPathResource = new ClassPathResource("/conf/xwork/ww-desktop.xml");
		Map<String, Object> map = null;
		try {
			map = Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile());
			classPathResource = new ClassPathResource("/conf/xwork/ww-officedoc-common.xml");
			map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
			classPathResource = new ClassPathResource("/conf/xwork/ww-officedoc-pigeonhole.xml");
			map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
			classPathResource = new ClassPathResource("/conf/xwork/ww-officedoc-receive.xml");
			map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
			classPathResource = new ClassPathResource("/conf/xwork/ww-officedoc-send.xml");
			map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
			classPathResource = new ClassPathResource("/conf/xwork/ww-officedoc-setting.xml");
			map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
			classPathResource = new ClassPathResource("/conf/xwork/ww-officedoc-remote.xml");
			map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
			//classPathResource = new ClassPathResource("/conf/xwork/ww-eis-system-desktop.xml");
			//map.putAll(Struts2XMLUtils.getStrut2XMLUrlHandler(classPathResource.getFile()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		registerHandlers(map);
	}

	protected void registerHandlers(Map<String, Object> handlerMap) {
		for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
			String url = entry.getKey();
			Object handler = entry.getValue();
			// Prepend with slash if not already present.
			if (!url.startsWith("/")) {
				url = "/" + url;
			}
			if (handler instanceof String) {
				handler = ((String) handler).trim();
			}
			registerHandler(url, handler);
		}
	}

	@Override
	protected Object getHandlerInternal(HttpServletRequest request) throws Exception {
		Object handler = super.getHandlerInternal(request);
		final String uri = request.getRequestURI();
		//处理如：http://127.0.0.1:8086/desktop/app/userInfo-remote!checkPassword.action
		if ( uri.contains("!")
				&& uri.endsWith(".action")
				&& handler == null ) {
			String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
			if ( handlerCache.get(uri) != null ) {
				return buildPathExposingHandler(handlerCache.get(uri), lookupPath, lookupPath, null);
			}
			lookupPath = lookupPath.substring(0, lookupPath.indexOf("!"));
			handler = lookupHandler(lookupPath + ".action", request);
			if ( handler instanceof HandlerExecutionChain
					&& ((HandlerExecutionChain)handler).getHandler() instanceof ActionProxy) {

				ActionProxy actionProxy = ((ActionProxy)((HandlerExecutionChain)handler).getHandler());
				//复制一份而不是直接修改MethodName
				final Struts2SpringMVCResultView struts2View = new Struts2SpringMVCResultView();
				Struts2SpringMVCResultView originView = actionProxy.getStruts2View();
				struts2View.setMethodName(uri.substring(uri.indexOf("!")+1, uri.lastIndexOf(".")));
				struts2View.setBeanFullName(originView.getBeanFullName());
				struts2View.setNameSpace(originView.getNameSpace());
				struts2View.setMethodUrl(uri.substring(uri.indexOf("!")+1));
				struts2View.setViews(originView.getViewMap());
				handlerCache.put(uri, struts2View);
				//注册
				Map<String, Object> haMap = new HashMap<String, Object>(1);
				haMap.put(uri, struts2View);
				registerHandlers(haMap);

				return buildPathExposingHandler(struts2View, lookupPath, lookupPath, null);
			}
		}

		//if ( handler instanceof HandlerExecutionChain) {
		//	Object actionHandler = ((HandlerExecutionChain)handler).getHandler();
		//	if ( actionHandler instanceof ActionProxy ) {
		//		ActionProxy actionProxy = (ActionProxy) actionHandler;
		//		Struts2SpringMVCAction action = actionProxy.getAction().getClass().getAnnotation(Struts2SpringMVCAction.class);
		//		if ( action == null ) {
		//			return null;
		//		}
		//	}
		//}
		return handler;
	}

	@Override
	protected Object buildPathExposingHandler(Object rawHandler, String bestMatchingPattern, String pathWithinMapping, Map<String, String> uriTemplateVariables) {
		HandlerExecutionChain chain = (HandlerExecutionChain) super.buildPathExposingHandler(rawHandler, bestMatchingPattern, pathWithinMapping, uriTemplateVariables);
		if ( rawHandler instanceof Struts2SpringMVCResultView) {
			Struts2SpringMVCResultView view = (Struts2SpringMVCResultView) rawHandler;
			Object actionProxy = new Struts2ActionProxy(getApplicationContext().getBean( view.getBeanName()), view);
			HandlerExecutionChain actionProxyChain = new HandlerExecutionChain(actionProxy, chain.getInterceptors());
			return actionProxyChain;
		}
		return chain;
	}
}
