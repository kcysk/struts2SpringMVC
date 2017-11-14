package net.zdsoft.struts2spring;

/**
 * @author shenke date 2017/11/9下午5:30
 */
public enum Protocol {

	HTTP("http"),
	HTTPS("https");

	private String value;

	Protocol(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean matcheIgnoreCase(String protocol) {
		return this.value.equalsIgnoreCase(protocol);
	}
}
