package com.mvwsolutions.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 
 * Spring component Service Use to get Spring component beans
 * 
 * 
 * @author smineyev
 * 
 */
public final class ComponentManager {

	private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
	
    public static final String BEAN_REF_CONTEXT_XML = "classpath:beanRefContext.xml";

    public static final String ROOT_BEAN_FACTORY_NAME = "default.context";

    private static ComponentManager instance;

    private static ThreadLocal<Object> initFlag = new ThreadLocal<Object>();

    protected ComponentManager() {
        beanFactory = (ApplicationContext) ContextSingletonBeanFactoryLocator
                .getInstance(BEAN_REF_CONTEXT_XML).useBeanFactory(
                        ROOT_BEAN_FACTORY_NAME).getFactory();
        
        logger.warn("<<< ComponentService (Spring) started >>>");
    }

    public static ComponentManager getInstance() {
        if (initFlag.get() == null) {
            synchronized (ComponentManager.class) {
                if (instance == null) {
                    instance = new ComponentManager();
                }
            }
            initFlag.set(ComponentManager.class);
        }
        return instance;
    }

    protected ApplicationContext beanFactory;


    public Object getComponentByName(String componentName) {
        return beanFactory.getBean(componentName);
    }

    public static <T> Map<String, T> getComponentsByType(Class<T> clazz) {
        return getInstance().beanFactory.getBeansOfType(clazz);
    }

    public static Object getComponent(String name) {
        return getInstance().getComponentByName(name);
    }
    
    public void shutdown() {
        synchronized (ComponentManager.class) {
            if (beanFactory instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext) beanFactory).close();
            }
            beanFactory = null;
            instance = null;
        }
    }
}
