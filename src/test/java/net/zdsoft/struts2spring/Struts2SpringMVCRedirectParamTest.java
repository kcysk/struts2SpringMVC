package net.zdsoft.struts2spring;

import net.zdsoft.desktop.action.DesktopAppAction;
import net.zdsoft.keel.util.URLUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shenke date 2017/11/9下午3:31
 */
public class Struts2SpringMVCRedirectParamTest {

	public static void main(String[] args) {
		//Map<String, String> paMap = URLUtils.getParameters("/sss/aa?aa=${sss}&ss=${pam2}");
		//for (Map.Entry<String, String> stringStringEntry : paMap.entrySet()) {
		//	System.out.println(stringStringEntry.getValue());
		//}
		//Field field = Reflection.getField(DesktopAppAction.class, "appId");
		//System.out.println(field);
	}
}
