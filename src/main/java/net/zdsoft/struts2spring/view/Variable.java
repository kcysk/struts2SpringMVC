package net.zdsoft.struts2spring.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记注解 由Spring进行参数绑定，不再使用
 */
@Deprecated
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Variable {

	Mode[] mode() default Mode.MODEL;

	enum Mode {
		MODEL,
		PARAMETERS
	}
}
