package esun.dbhelper;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import esun.dbhelper.dataSources.DynamicDataSourceConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@EntityScan("esun.dbhelper.dao.mapper")
@MapperScan("esun.dbhelper.dao.mapper")
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@Import({DynamicDataSourceConfig.class})
@EnableDiscoveryClient
@EnableApolloConfig
public class DbhelperApplication {
    public static void main(String[] args) {
        //跳过服务发现
        System.setProperty("apollo.configService", "http://10.124.0.47:30002");
        SpringApplication.run(DbhelperApplication.class, args);
    }

}
