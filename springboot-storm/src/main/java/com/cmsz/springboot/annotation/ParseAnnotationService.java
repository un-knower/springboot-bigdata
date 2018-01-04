package com.cmsz.springboot.annotation;

import java.util.Map;

/**
 * Created by le on 2018/1/4.
 */
public interface ParseAnnotationService {
    /*
  * 获取本身类及超类的上属性key->value值方法
  * */
    Map<String,Object> gainSuperFieldValue(Object object) throws IllegalAccessException;

    /*
    * 获取本身类及超类的上属性key->value值方法
    * */
    Map<String,Object> gainFieldValue(Object object) throws IllegalAccessException;

    /*获取类中对象属性*/
    Map<String,Object> gainFieldProperties(Object object) throws IllegalAccessException;
}
