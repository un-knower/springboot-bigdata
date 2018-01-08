package com.cmsz.springboot;

import com.cmsz.springboot.service.storm.StormOperateServer;
import org.junit.Test;

import javax.annotation.Resource;

public class SpringbootStormApplicationTests {

	@Resource(name = "stormOperateService")
    private StormOperateServer stormOperateServer;

	@Test
    public void testStorm() throws Exception {
        stormOperateServer.autoBuildStormTopology("SpringStorm","local");
    }
}
