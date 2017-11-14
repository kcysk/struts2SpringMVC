package net.zdsoft.struts2spring;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shenke date 2017/11/8下午2:23
 */
public class Struts2SpringMVCResultView implements Serializable {

	private Map<String, View> viewMap;

	private String methodName;
	private String methodUrl;
	private String beanName;
	private String beanFullName;
	private String nameSpace;

	public Struts2SpringMVCResultView() {
		this.viewMap = new HashMap<String, View>(16);
	}

	public static class View {
		private String viewName;
		private ResponseType viewType;
		private String resultName;

		public View() {
		}

		public View(String viewName, ResponseType viewType, String resutlName) {
			this.viewName = viewName;
			this.viewType = viewType;
			this.resultName = resutlName;
		}

		public String getViewName() {
			return viewName;
		}

		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		public ResponseType getViewType() {
			return viewType;
		}

		public void setViewType(ResponseType viewType) {
			this.viewType = viewType;
		}

		public String getResultName() {
			return resultName;
		}

		public void setResultName(String resultName) {
			this.resultName = resultName;
		}
	}

	public View getView(String resultName) {
		View view = this.viewMap.get(resultName);
		return view == null ? Struts2XMLUtils.GlobalView.get(resultName) : view;
	}


	public void setView(View view) {
		this.viewMap.put(view.getResultName(), view);
	}

	public void setViews(Map<String, View> views) {
		this.viewMap.putAll(views);
	}

	public Map<String, View> getViewMap() {
		return this.viewMap;
	}

	public String getMethodName() {
		return methodName;
	}

	public Struts2SpringMVCResultView setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}

	public String getMethodUrl() {
		return methodUrl;
	}

	public Struts2SpringMVCResultView setMethodUrl(String methodUrl) {
		this.methodUrl = methodUrl;
		return this;
	}

	public String getBeanName() {
		return beanName;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public Struts2SpringMVCResultView setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
		return this;
	}

	public String getBeanFullName() {
		return beanFullName;
	}

	public Struts2SpringMVCResultView setBeanFullName(String beanFullName) {
		this.beanFullName = beanFullName;
		String beanNameTemp = beanFullName.substring(beanFullName.lastIndexOf(".")+1);
		this.beanName = String.valueOf(beanNameTemp.charAt(0)).toLowerCase() + beanNameTemp.substring(1);
		return this;
	}
}
