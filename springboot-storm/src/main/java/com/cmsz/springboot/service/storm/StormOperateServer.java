package com.cmsz.springboot.service.storm;

import org.apache.storm.Config;

/**
 * Created by le on 2018/1/4.
 */
public interface StormOperateServer {
    /*自动构建topology*/
    void autoBuildStormTopology(String topologyName, Config conf, String modelType) throws Exception;
}
