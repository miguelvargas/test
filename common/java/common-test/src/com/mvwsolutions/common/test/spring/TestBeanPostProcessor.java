package com.mvwsolutions.common.test.spring;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TestBeanPostProcessor implements BeanPostProcessor {
	private static Log logger = LogFactory.getLog(TestBeanPostProcessor.class);
    
    // integration tests may want to disable this processot in order to test "real" beans
    public static boolean ENABLED = true; 

	private Map<String, Object> classReplacementMap = new HashMap<String, Object>(); 
	private Map<String, Object> beanReplacementMap = new HashMap<String, Object>(); 
	
	public Map<String, Object> getBeanReplacementMap() {
		return beanReplacementMap;
	}

	public void setBeanReplacementMap(Map<String, Object> beanReplacementMap) {
		this.beanReplacementMap = beanReplacementMap;
	}

	public Map<String, Object> getClassReplacementMap() {
		return classReplacementMap;
	}

	public void setClassReplacementMap(Map<String, Object> classReplacementMap) {
		this.classReplacementMap = classReplacementMap;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		Object res = getBeanImpl(bean, beanName);

		return res;
	}

	private Object getBeanImpl(Object bean, String beanName) {
        if (!ENABLED) {
            return bean;
        }
        
		Object res;
		res = classReplacementMap.get(bean.getClass().getName());
		if (res != null) {
			logger.warn("Bean (" + beanName + " - " + bean.getClass().getName()
					+ ") is replaced with class ("+res.getClass().getName()+")");
		} else {
			res = beanReplacementMap.get(beanName);
			if (res != null) {
				logger.warn("Bean (" + beanName + " - " + bean.getClass().getName()
						+ ") is replaced with class ("+res.getClass().getName()+")");
			}
		}
		return res != null ? res : bean;
	}

}
