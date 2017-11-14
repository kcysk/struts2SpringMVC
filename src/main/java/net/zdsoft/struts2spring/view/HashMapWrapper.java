package net.zdsoft.struts2spring.view;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MapModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;

import java.util.Map;
import java.util.Set;

/**
 * freemarker hashMap wrapper,兼容struts2的Map写法
 * @author shenke date 2017/11/13上午9:55
 */
public class HashMapWrapper extends MapModel implements TemplateHashModelEx {

	static final ModelFactory FACTORY = new ModelFactory() {
		@Override
		public TemplateModel create(Object object, ObjectWrapper wrapper) {
			return new HashMapWrapper((Map) object, (BeansWrapper) wrapper);
		}
	};

	public HashMapWrapper(Map map, BeansWrapper wrapper) {
		super(map, wrapper);
	}

	@Override
	public boolean isEmpty() {
		return ((Map) object).isEmpty();
	}

	@Override
	protected Set keySet() {
		return ((Map) object).keySet();
	}

	@Override
	public TemplateCollectionModel values() {
		return new CollectionAndSequence(new SimpleSequence(((Map) object).values(), wrapper));
	}
}
