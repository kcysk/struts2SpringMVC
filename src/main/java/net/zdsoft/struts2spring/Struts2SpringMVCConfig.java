package net.zdsoft.struts2spring;

import freemarker.template.TemplateException;
import net.zdsoft.eis.base.common.util.AppSetting;
import net.zdsoft.eis.base.common.util.AppSettingFreemarkerManager;
import net.zdsoft.struts2spring.view.Struts2FreemarkerViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * spring 视图配置 ，拦截器等配置
 * @author shenke date 2017/11/9上午10:14
 */
@Configuration
public class Struts2SpringMVCConfig {

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer() throws IOException, TemplateException {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		//configurer.setTemplateLoaderPath("/");
		configurer.setFreemarkerVariables(new HashMap<String, Object>(1){{
			put(AppSettingFreemarkerManager.KEY_WEBCONFIG, AppSetting.getInstance());
		}});
		Properties setting = new Properties();
		setting.setProperty("template_update_delay", "0");
		setting.setProperty("defaultEncoding", "UTF-8");
		setting.setProperty("url_escaping_charset", "UTF-8");
		setting.setProperty("locale", "zh_CN");
		setting.setProperty("boolean_format", "true,false");
		setting.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
		setting.setProperty("date_format", "yyyy-MM-dd");
		setting.setProperty("time_format", "HH:mm:ss");
		setting.setProperty("number_format", "0.######");
		setting.setProperty("whitespace_stripping","true");
		configurer.setFreemarkerSettings(setting);
		configurer.setTemplateLoaderPaths(new String[]{"/",""});
		return configurer;
	}

	@Bean
	public FreeMarkerViewResolver freeMarkerViewResolver() {
		FreeMarkerViewResolver viewResolver = new Struts2FreemarkerViewResolver();
		viewResolver.setSuffix("");
		viewResolver.setCache(true);
		viewResolver.setOrder(2);
		viewResolver.setContentType("text/html;charset=UTF-8");
		viewResolver.setExposeRequestAttributes(true);
		viewResolver.setExposeSessionAttributes(true);
		viewResolver.setExposeSpringMacroHelpers(true);
		viewResolver.setRequestContextAttribute("request");
		return viewResolver;
	}

	//FIXME request经过Spring处理过之后，原来文件上传的写法需要改写
	@Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
	public CommonsMultipartResolver commonsMultipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		//resolver.setMaxUploadSize(100);
		return resolver;
	}
}
