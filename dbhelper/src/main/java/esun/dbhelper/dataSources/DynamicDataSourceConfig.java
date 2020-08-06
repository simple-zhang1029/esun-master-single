package esun.dbhelper.dataSources;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置多数据源
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/8/19 0:41
 */
@Configuration
public class DynamicDataSourceConfig {


    @Bean
    @ConfigurationProperties("spring.datasource.druid.postgres.master")
    public DataSource postgresMasterDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.postgres.slave")
    public DataSource postgresSlaveDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.mysql")
    public DataSource mysqlDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource postgresMasterDataSource,DataSource postgresSlaveDataSource,DataSource mysqlDataSource) {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.POSTGRES_MASTER, postgresMasterDataSource);
        targetDataSources.put(DataSourceNames.POSTGRES_SLAVE, postgresSlaveDataSource);
        targetDataSources.put(DataSourceNames.MYSQL,mysqlDataSource);
        DynamicDataSource dynamicDataSource=new DynamicDataSource(postgresSlaveDataSource,targetDataSources);

        return dynamicDataSource;
    }


}
