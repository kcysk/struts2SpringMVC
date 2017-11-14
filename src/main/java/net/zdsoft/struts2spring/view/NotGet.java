package net.zdsoft.struts2spring.view;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 现在创建渲染Freemarker已不需要手动反射获调用所有的GET方法
 * 利用Spring EL 按需渲染，该注解也不要再使用了
 * @author shenke date 2017/11/9下午4:01
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NotGet {

}
