package com.cmsz.springboot.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Created by le on 2017/7/4.
 */
@Configuration
@PropertySource(value = {"file:D:/btmspath/mysql.properties"})
//@PropertySource(value = {"file:${btmspath}/mysql.properties"})
public class DBSource {

    @Value("${jdbc.driver}")
    private String driver;

//    @Value("${jdbc.url}")
    private String url="jdbc:mysql://47.104.18.57:3306/test?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true";

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

   /* @Autowired
    private Environment env;*/

    @Bean(name = "dataSource")
    public DruidDataSource getDataSource() throws Exception {
        DruidDataSource basicDataSource= new DruidDataSource();
        basicDataSource.setDriverClassName(driver);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        return basicDataSource;
    }
}
