package net.zdsoft.struts2spring;

import com.opensymphony.xwork2.util.DomHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.zdsoft.struts2spring.Struts2XMLConstant.*;

/**
 * 读取struts2 XML配置文件工具类
 *
 * @author shenke date 2017/11/8下午12:27
 */
final class Struts2XMLUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Struts2XMLUtils.class);


	private static final Map<String, Object> handlerCache = new HashMap<String, Object>(16);

	static Map<String, Struts2SpringMVCResultView.View> GlobalView ;
	static {
		GlobalView = new HashMap<String, Struts2SpringMVCResultView.View>(6) {{
			put("none", new Struts2SpringMVCResultView.View("/common/none.ftl", ResponseType.FREEMARKER, "none"));
			put("exception", new Struts2SpringMVCResultView.View("/common/exception.ftl", ResponseType.FREEMARKER, "exception"));
			put("promptMsg", new Struts2SpringMVCResultView.View("/common/promptmessage.ftl", ResponseType.FREEMARKER, "promptMsg"));
			put("nopermission", new Struts2SpringMVCResultView.View("/common/nopermission.ftl", ResponseType.FREEMARKER, "nopermission"));
			put("commonTree", new Struts2SpringMVCResultView.View("/common/xtree/commontree.ftl", ResponseType.FREEMARKER, "commonTree"));
			put("commonTreeXml", new Struts2SpringMVCResultView.View("/common/xtree/commontreexml.ftl", ResponseType.FREEMARKER, "commonTreeXml"));
			put("commonTree2", new Struts2SpringMVCResultView.View("/common/popupObjectDiv.ftl", ResponseType.FREEMARKER, "commonTree2"));
			put("commonTree3", new Struts2SpringMVCResultView.View("/common/ztree/ztree.ftl", ResponseType.FREEMARKER, "commonTree3"));
		}};
	}

	public static Map<String, Object> getStrut2XMLUrlHandler(File file) {
		//解析struts2 XML
		return parse(file);
	}

	private static Map<String,Object> parse(final File file) {
		try {
			Map<String, Object> urlClassMap = new HashMap<String, Object>(16);
			List<File> fileList;
			if (file.isDirectory()) {
				fileList = Arrays.asList(file.listFiles());
			} else {
				fileList = new ArrayList<File>(1){{
					add(file);
				}};
			}
			for (File f : fileList) {
				FileInputStream inputStream = new FileInputStream(f);
				Document document = DomHelper.parse(new InputSource(inputStream));
				Element element = document.getDocumentElement(); //struts
				NodeList packageList = element.getChildNodes();
				for (int packageNum = 0; packageNum < packageList.getLength(); packageNum++) {
					Element child = (Element) packageList.item(packageNum);
					urlClassMap.putAll(parsePackage(child));
				}
			}
			return urlClassMap;
		} catch (IOException e) {
			return null;
		}
	}

	private static Map<String, Struts2SpringMVCResultView> parsePackage(Element packageNode) {
		Map<String, Struts2SpringMVCResultView> mvcModelAndViewMap = new HashMap<String, Struts2SpringMVCResultView>(16);
		String actionRequestMapping = packageNode.getAttribute(ATTR_NAME_SPACE);
		NodeList actionList = packageNode.getChildNodes();
		for (int i = 0; i < actionList.getLength(); i++) {
			Element child = (Element) actionList.item(i);

			String methodUrl = child.getAttribute(ATTR_NAME);
			String beanName = child.getAttribute(ATTR_CLASS);
			String methodName = child.getAttribute(ATTR_METHOD);
			methodName = StringUtils.isBlank(methodName) ? DEFAULT_METHOD_NAME : methodName;

			Struts2SpringMVCResultView mvcModelAndView = new Struts2SpringMVCResultView();
			mvcModelAndView.setMethodUrl(methodUrl)
					.setBeanFullName(beanName)
					.setMethodName(methodName)
					.setNameSpace(actionRequestMapping);
			parseResult(child.getChildNodes(), mvcModelAndView);
			LOGGER.debug(beanName + "--" + methodUrl);

			String key = actionRequestMapping + URL_SEPARATOR_SIGN + methodUrl + STRUTS2_SUFFIX_ACTION;
			mvcModelAndViewMap.put(key, mvcModelAndView);
		}
		return mvcModelAndViewMap;
	}

	private static void parseResult(NodeList resultNodes, Struts2SpringMVCResultView mvcModelAndView) {
		if ( resultNodes == null || resultNodes.getLength() == 0 ) {
			return ;
		}
		for (int i = 0; i < resultNodes.getLength(); i++) {
			Element result = (Element) resultNodes.item(i);
			if ( result.getNodeName().equals("interceptor-ref")) {
				LOGGER.error("Struts2 Interceptor 已不再被支持，请改为Spring的拦截器");
				continue;
			}
			String resultName = result.getAttribute(ATTR_NAME);
			String resultValue = null;
			String type = result.getAttribute(ATTR_TYPE);
			Map<String, String> paramMap;
			if (RESULT_TYPE_REDIRECT.equals(type) || RESULT_TYPE_REDIRECT_ACTION.equals(type)) {
				paramMap = parseParam(result.getChildNodes());
				StringBuilder redirectUrl = new StringBuilder();
				String action = paramMap.get("actionName");
				redirectUrl.append(action).append(RESULT_TYPE_REDIRECT_ACTION.equals(type)?STRUTS2_SUFFIX_ACTION:"").append(URL_QUESTION_MARK);
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					if ( entry.getKey().equals("actionName")) {
						continue;
					}
					redirectUrl.append(redirectUrl.indexOf(URL_QUESTION_MARK) == redirectUrl.length()-1 ? "":URL_AND_SIGN);
					redirectUrl.append(entry.getKey()).append(URL_EQUALS_SIGN).append(entry.getValue());
				}
				resultValue = redirectUrl.toString();
			}
			else if(RESULT_TYPE_FREEMARKER.equals(type)) {
				//..
				String relativePath = result.getChildNodes().item(0).getNodeValue();
				//resultValue = mvcModelAndView.getNameSpace() + URL_SEPARATOR_SIGN + ;
				resultValue = getRealPath(mvcModelAndView.getNameSpace(), relativePath);
			}
			else if(RESULT_TYPE_JSON.equals(type)){
				paramMap = parseParam(result.getChildNodes());
				Struts2JSONResultView view = new Struts2JSONResultView();
				if ( paramMap.isEmpty()) {
					mvcModelAndView.setView(buildDefaultJSONView());
				}
				else {
					view.setViewName(paramMap.get(PARAM_ROOT));
					view.setResultName(result.getAttribute(ATTR_NAME));
					view.setViewType(ResponseType.JSON);
					view.setNoCache(Boolean.valueOf(paramMap.get(PARAM_NO_CACHE)));
					view.setContentType(paramMap.get(PARAM_CONTENTTYPE));
				}
				mvcModelAndView.setView(view);
				return;
			} else {
				Node node = result.getChildNodes().item(0);
				resultValue = node != null ? node.getNodeValue() : null;
			}
			Struts2SpringMVCResultView.View view = new Struts2SpringMVCResultView.View(resultValue, ResponseType.valueOfType(type), resultName);
			mvcModelAndView.setView(view);
		}
	}

	private static Map<String, String> parseParam(NodeList paramNodes) {
		Map<String, String> paramMap = new HashMap<String, String>(16);
		if ( paramNodes == null || paramNodes.getLength() == 0) {
			return paramMap;
		}
		for (int i = 0; i < paramNodes.getLength(); i++) {
			Node param = paramNodes.item(i);
			if ( Node.ELEMENT_NODE == param.getNodeType() && TAG_PARAM.equals(param.getNodeName())) {
				Element element = (Element)param;
				paramMap.put(element.getAttribute(ATTR_NAME), element.getChildNodes().item(0).getNodeValue());
			}
		}
		return paramMap;
	}

	private static Struts2SpringMVCResultView.View buildDefaultJSONView() {
		return new Struts2JSONResultView();
	}

	private static String getRealPath(String nameSpace, String freemarkerResult) {
		if ( freemarkerResult.startsWith("../")) {
			return getRealPath(nameSpace.substring(0,nameSpace.lastIndexOf(URL_SEPARATOR_SIGN)),
					freemarkerResult.substring(freemarkerResult.indexOf(URL_SEPARATOR_SIGN)+1));
		} else {
			return nameSpace + URL_SEPARATOR_SIGN + freemarkerResult;
		}
	}
}
