package net.zdsoft.struts2spring;

import net.zdsoft.keel.util.URLUtils;
import net.zdsoft.struts2spring.binder.ActionDataBinder;
import net.zdsoft.struts2spring.invoke.ActionProxy;
import net.zdsoft.struts2spring.result.ExcelErrorDataResultHandler;
import net.zdsoft.struts2spring.result.ExcelResultHandler;
import net.zdsoft.struts2spring.result.JSONResultHandler;
import net.zdsoft.struts2spring.view.ExpressionHashModel;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author shenke date 2017/11/8上午11:42
 */
@Component
public class Struts2SpringMVCHandlerAdapter implements  HandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(Struts2SpringMVCHandlerAdapter.class);

	@Resource
	private JSONResultHandler jsonResultHandler;
	@Resource
	private ExcelResultHandler excelResultHandler;
	@Resource
	private ExcelErrorDataResultHandler excelErrorDataResultHandler;

	@Override
	public boolean supports(Object o) {
		return true;
	}

	@Override
	public ModelAndView handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)  {
		ModelAndView mv ;
		ActionProxy actionProxy = null;
		try {
			Struts2SpringMVCResponseHolder.bindResponse(httpServletResponse);
			if ( o instanceof ActionProxy ) {
				actionProxy = (ActionProxy) o;
			}
			Assert.notNull(actionProxy);

			//bind request parameters to action
			bindParameters(actionProxy, httpServletRequest);

			//invoke action method
			Object result = actionProxy.invoke();

			mv = getModelAndView(actionProxy, result, httpServletRequest, httpServletResponse);
		} catch ( Exception e) {
			mv = new ModelAndView(Struts2XMLUtils.GlobalView.get("exception").getViewName());
			mv.addObject("exception", e.getMessage());
			mv.addObject("exceptionStack", ExceptionUtils.getFullStackTrace(e));
			mv.addObject(ExpressionHashModel.MODEL_ACTION_PROXY_ATTRIBUTE_NAME, actionProxy);
			mv.addObject("linkURL", httpServletRequest.getRequestURL().toString());
			ExceptionUtils.printRootCauseStackTrace(e);
			return mv;
		}
		finally {
			Struts2SpringMVCResponseHolder.resetResponse();
		}
		return mv;
	}

	@Override
	public long getLastModified(HttpServletRequest httpServletRequest, Object o) {
		return -1L;
	}

	//FIXME 日期未处理
	private void bindParameters(ActionProxy actionProxy, HttpServletRequest request )
			throws InvocationTargetException, IllegalAccessException {
		ActionDataBinder dataBinder = new ActionDataBinder(actionProxy.getAction(), actionProxy.getStruts2View().getBeanName());
		dataBinder.bind(request);
	}

	private ModelAndView getModelAndView(ActionProxy actionProxy, Object returnValue,
										 HttpServletRequest request,
										 HttpServletResponse response) throws Exception {
		if ( returnValue == null ) {
			return null;
		} else if ( returnValue instanceof String ) {
			Struts2SpringMVCResultView.View view = actionProxy.getStruts2View().getView((String) returnValue);
			if ( ResponseType.FREEMARKER.equals(view.getViewType())
					|| ResponseType.CHINA_EXCEL.equals(view.getViewType())) {
				ModelAndView mv = new ModelAndView(view.getViewName());
				mv.addObject(ExpressionHashModel.MODEL_ACTION_PROXY_ATTRIBUTE_NAME, actionProxy);
				return mv;
			} else if ( ResponseType.JSON.equals(view.getViewType()) ) {
				handleResponseBody(actionProxy, view);
			} else if ( ResponseType.REDIRECT.equals(view.getViewType()) ) {
				handleRedirect(actionProxy, request, response, (String) returnValue);
			} else if ( ResponseType.REDIRECT_ACTION.equals(view.getViewType()) ) {
				handleRedirect(actionProxy, request, response, (String) returnValue);
			} else if ( ResponseType.STREAM.equals(view.getViewType()) ) {
				throw new IllegalArgumentException("not support");
			} else if ( ResponseType.DISPATCHER.equals(view.getViewType())) {
				throw new IllegalArgumentException("not support");
			} else if ( ResponseType.EXCEL.equals(view.getViewType())) {
				excelResultHandler.execute(actionProxy.getAction(), "excelTemplateUtil");
			} else if ( ResponseType.EXCEL_ERROR_DATA.equals(view.getViewType())) {
				excelErrorDataResultHandler.execute(actionProxy.getAction(), view.getViewName());
			} else {
				throw new IllegalArgumentException("not support type");
			}
		} else {
			throw new IllegalArgumentException("not support return type");
		}
		return null;
	}

	/**
	 * 处理JSON类型的消息
	 */
	private void handleResponseBody(Object actionProxy, Struts2SpringMVCResultView.View view) {
		Struts2JSONResultView jsonResultView = ((Struts2JSONResultView)view);
		if ( jsonResultView.getContentType() == null ) {
			jsonResultView.setContentType("application/json");
		}
		jsonResultHandler.execute(actionProxy, view);
	}

	private void handleRedirect(ActionProxy actionProxy, HttpServletRequest request,
								  HttpServletResponse response, String returnValue) throws Exception {
		Struts2SpringMVCResultView view = actionProxy.getStruts2View();
		String viewName = view.getView(returnValue).getViewName();
		Object action = actionProxy.getAction();
		//处理参数
		if ( viewName.startsWith(Struts2XMLConstant.PARAM_$)) {
			viewName = viewName.substring(2, viewName.length()-1);
			viewName = BeanUtils.getProperty(action, viewName);
		}

		//FIXME 改为Spring EL
		if ( viewName.contains(Struts2XMLConstant.PARAM_$)) {
			Map<String, String> parameterMap = URLUtils.getParameters(viewName);
			for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
				String property = entry.getValue().substring(2, entry.getValue().length()-1);
				viewName = viewName.replace(entry.getValue(), BeanUtils.getProperty(action, property));
			}
		}

		if (Protocol.HTTP.matcheIgnoreCase(viewName)
				|| Protocol.HTTPS.matcheIgnoreCase(viewName)) {
			response.sendRedirect(viewName);
		}
		//Action 内部跳转
		else if ( !viewName.startsWith(Struts2XMLConstant.URL_SEPARATOR_SIGN) ) {
			response.sendRedirect(getPrefix(request) + Struts2XMLConstant.URL_SEPARATOR_SIGN
					+ view.getNameSpace() + Struts2XMLConstant.URL_SEPARATOR_SIGN + viewName);
		}
		//Action
		else {
			response.sendRedirect(getPrefix(request) + Struts2XMLConstant.URL_SEPARATOR_SIGN + viewName);
		}
	}

	private String getPrefix(HttpServletRequest request) {
		return request.getScheme() +"://" + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath();
	}

}
