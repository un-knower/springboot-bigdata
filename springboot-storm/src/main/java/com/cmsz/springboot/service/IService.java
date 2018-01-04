package com.cmsz.springboot.service;

import java.lang.annotation.Annotation;
import java.rmi.Remote;
import java.util.List;

/**
 * Created by le on 2018/1/4.
 */
public interface IService extends Remote {
    public List<Class<?>> findTargetClass(String packageName, Class<? extends Annotation> annotationClass);
}
