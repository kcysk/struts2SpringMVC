### 说明 
* 针对ResponseType中的几种类型，处理了Excel，ChinaExcel，ExcelErrorData， Freemarker，JSON，Redirect，RedirectAction等几种类型；
  Freemarker，JSON等进行过测试；
* 数据绑定是由ActionDataBinder完成的（完全交由Spring处理），需要做并发测试并需要留意性能问题；
* 渲染Freemarker则利用了Spring EL，将Action作为RootObject；
* 需要Action增加注解：@Controller @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE) @Struts2SpringMVCAction
* Action 中service需要加@Autowired注解（也可以不加，我们额外做一步注入操作，目前这部分代码没有写）

* 目前的代码不太具有通用性,部分代码属数字校园特殊写法
		-- 2017.11.14