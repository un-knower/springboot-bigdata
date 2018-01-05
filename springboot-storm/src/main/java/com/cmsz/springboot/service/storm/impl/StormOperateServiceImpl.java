package com.cmsz.springboot.service.storm.impl;

import com.cmsz.springboot.service.storm.StormOperateServer;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by le on 2017/9/27.
 */
@Service(value = "stormOperateService")
public class StormOperateServiceImpl  extends StormServerImpl implements StormOperateServer {

    @Override
    public void autoBuildStormTopology(String topologyName, Config conf,String modelType) throws Exception{
        if(modelType.equals("local")){
           buildStormLocalType(topologyName,conf);
        }else{
            StormSubmitter.submitTopology(topologyName, conf, buildStormTopology());
        }
    }

    @Override
    public void autoBuildStormTopology(String topologyName, String modelType) throws Exception {
        /*配置storm config*/
        Config conf=new Config();
        conf.setNumAckers(1);
        conf.setDebug(true);
        conf.setMaxTaskParallelism(1);
        if(modelType.equals("local")){
           buildStormLocalType(topologyName,conf);
        }else{
            StormSubmitter.submitTopology(topologyName, conf, buildStormTopology());
        }
    }

    private void buildStormLocalType(String topologyName,Config conf)throws Exception{
         /*本地调试模式*/
        LocalCluster cluster = new LocalCluster();
            /*自动构建topology图*/
        StormTopology stormTopology=buildStormTopology();
        cluster.submitTopology(topologyName, conf, stormTopology);
        System.out.println("===============构建storm topology 成功======================");
        Utils.sleep(100000);
        System.out.println("===============关闭storm topology======================");
        cluster.killTopology(topologyName);
        cluster.shutdown();
    }

}
