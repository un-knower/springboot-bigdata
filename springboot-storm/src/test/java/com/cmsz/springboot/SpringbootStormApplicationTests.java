package com.cmsz.springboot;

import com.cmsz.springboot.service.storm.StormOperateServer;
import org.apache.storm.Config;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootStormApplicationTests {

	@Resource(name = "stormOperateService")
    private StormOperateServer stormOperateServer;

	@Test
    public void testStorm() throws Exception {
        stormOperateServer.autoBuildStormTopology("SpringStorm","local");
    }
}
