package esun.token;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@ComponentScan("esun.token")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@Configuration
@EnableApolloConfig
public class TokenApplication {
    public static void main(String[] args) {
        System.setProperty("apollo.configService", "http://10.124.0.47:30002");
        SpringApplication.run(TokenApplication.class, args);
    }

}
