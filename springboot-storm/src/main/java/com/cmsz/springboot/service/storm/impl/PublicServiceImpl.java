package com.cmsz.springboot.service.storm.impl;

import com.cmsz.springboot.service.IService;
import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.PackageNameFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by le on 2018/1/4.
 */
public abstract class PublicServiceImpl implements IService {
    private static Logger logger = LoggerFactory.getLogger(PublicServiceImpl.class);

    @Override
    public  List<Class<?>> findTargetClass(String packageName,Class<? extends Annotation> annotationClass) {
        List<Class<?>> valueClass=new ArrayList<Class<?>>();
        List<Class<?>> classes = CPScanner.scanClasses(
                new PackageNameFilter(packageName));
        for(Class<?> clazz: classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                valueClass.add(clazz);
            }
        }
        return valueClass;
    }

}
