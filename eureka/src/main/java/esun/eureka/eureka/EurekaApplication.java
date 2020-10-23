package esun.eureka.eureka;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@EnableDiscoveryClient
@EnableApolloConfig
public class EurekaApplication {

    public static void main(String[] args) {
//        System.setProperty("apollo.configService", "http://10.124.0.99:8080");
        SpringApplication.run(EurekaApplication.class, args);
    }

}
