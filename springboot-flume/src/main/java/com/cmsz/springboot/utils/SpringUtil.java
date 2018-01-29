package com.cmsz.springboot.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by le on 2017/9/17.
 */
@Configuration
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final String  conf = "dispatcher-servlet.xml";

    private static synchronized  void SpringContextInit() {
        if (applicationContext == null) {applicationContext = new ClassPathXmlApplicationContext(conf);}
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(null== SpringUtil.applicationContext){
            SpringUtil.applicationContext=applicationContext;
        }
    }

    public static <T> T getBeanByClass(Class<T> object){
        return  applicationContext.getBean(object);
    }

    public static <T> T getBeanByName(String name,Class <T> object){
        if (applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext(conf);
        }
        return  applicationContext.getBean(name,object);
    }


}
