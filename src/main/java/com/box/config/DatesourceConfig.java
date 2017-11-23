package com.box.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/8/28 0028.
 */
@Configuration
public class DatesourceConfig {

    @Bean(name="druidDataSource")
    public DruidDataSource druidDataSource(@Value("${spring.datasource.driver-class-name}") String driver,
                                           @Value("${spring.datasource.url}") String url,
                                           @Value("${spring.datasource.username}") String username,
                                           @Value("${spring.datasource.password}") String password,
                                           @Value("${spring.datasource.initSize}") Integer initSize,
                                           @Value("${spring.datasource.minIdle}") Integer minIdle,
                                           @Value("${spring.datasource.maxActive}") Integer maxActive
    ) {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);

        druidDataSource.setInitialSize(initSize);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);

        try {
            druidDataSource.setFilters("stat,wall");
            druidDataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000");
        } catch(Exception e){
        }

        return druidDataSource;
    }
}
