package esun.core;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan("esun.core")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableApolloConfig
public class CoreApplication {

    public static void main(String[] args) {
//        System.setProperty("apollo.configService", "http://10.124.0.99:8080");
        SpringApplication.run(CoreApplication.class, args);
    }

}
