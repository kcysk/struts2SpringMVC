package net.zdsoft.struts2spring;

import java.util.List;
import java.util.regex.Pattern;

/**
 * struts2 用户扩展的result Type类型
 * JSON, Excel, ChinaExcel,除JSON由自行处理外，其他两者需适配
 * @author shenke date 2017/11/9下午6:21
 */
public class Struts2JSONResultView extends Struts2SpringMVCResultView.View {

	private String className;
	private String encoding;
	private String defaultEncoding = "UTF-8";
	private List<Pattern> includeProperties;
	private List<Pattern> excludeProperties;
	private String root = "jsonMessageDto"; //struts.xml
	private boolean wrapWithComments;
	private boolean prefix;
	private boolean enableSMD = false;
	private boolean enableGZIP = false;
	private boolean ignoreHierarchy = true;
	private boolean ignoreInterfaces = true;
	private boolean enumAsBean = true;		//struts.xml
	private boolean noCache = false;
	private boolean excludeNullProperties = false;
	private String defaultDateFormat = null;
	private int statusCode;
	private int errorCode;
	private String callbackParameter;
	private String contentType;
	private String wrapPrefix;
	private String wrapSuffix;

	public Struts2JSONResultView() {
		setViewType(ResponseType.JSON);
	}

	public Struts2JSONResultView(String viewName, ResponseType viewType, String resutlName) {
		super(viewName, viewType, resutlName);
		this.root = viewName;
		setViewType(ResponseType.JSON);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getEncoding() {
		return encoding == null ? defaultEncoding : encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public List<Pattern> getIncludeProperties() {
		return includeProperties;
	}

	public void setIncludeProperties(List<Pattern> includeProperties) {
		this.includeProperties = includeProperties;
	}

	public List<Pattern> getExcludeProperties() {
		return excludeProperties;
	}

	public void setExcludeProperties(List<Pattern> excludeProperties) {
		this.excludeProperties = excludeProperties;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public boolean isWrapWithComments() {
		return wrapWithComments;
	}

	public void setWrapWithComments(boolean wrapWithComments) {
		this.wrapWithComments = wrapWithComments;
	}

	public boolean isPrefix() {
		return prefix;
	}

	public void setPrefix(boolean prefix) {
		this.prefix = prefix;
	}

	public boolean isEnableSMD() {
		return enableSMD;
	}

	public void setEnableSMD(boolean enableSMD) {
		this.enableSMD = enableSMD;
	}

	public boolean isEnableGZIP() {
		return enableGZIP;
	}

	public void setEnableGZIP(boolean enableGZIP) {
		this.enableGZIP = enableGZIP;
	}

	public boolean isIgnoreHierarchy() {
		return ignoreHierarchy;
	}

	public void setIgnoreHierarchy(boolean ignoreHierarchy) {
		this.ignoreHierarchy = ignoreHierarchy;
	}

	public boolean isIgnoreInterfaces() {
		return ignoreInterfaces;
	}

	public void setIgnoreInterfaces(boolean ignoreInterfaces) {
		this.ignoreInterfaces = ignoreInterfaces;
	}

	public boolean isEnumAsBean() {
		return enumAsBean;
	}

	public void setEnumAsBean(boolean enumAsBean) {
		this.enumAsBean = enumAsBean;
	}

	public boolean isNoCache() {
		return noCache;
	}

	public void setNoCache(boolean noCache) {
		this.noCache = noCache;
	}

	public boolean isExcludeNullProperties() {
		return excludeNullProperties;
	}

	public void setExcludeNullProperties(boolean excludeNullProperties) {
		this.excludeNullProperties = excludeNullProperties;
	}

	public String getDefaultDateFormat() {
		return defaultDateFormat;
	}

	public void setDefaultDateFormat(String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getCallbackParameter() {
		return callbackParameter;
	}

	public void setCallbackParameter(String callbackParameter) {
		this.callbackParameter = callbackParameter;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getWrapPrefix() {
		return wrapPrefix;
	}

	public void setWrapPrefix(String wrapPrefix) {
		this.wrapPrefix = wrapPrefix;
	}

	public String getWrapSuffix() {
		return wrapSuffix;
	}

	public void setWrapSuffix(String wrapSuffix) {
		this.wrapSuffix = wrapSuffix;
	}
}
