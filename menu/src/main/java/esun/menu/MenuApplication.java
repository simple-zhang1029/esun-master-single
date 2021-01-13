package esun.menu;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableApolloConfig
public class MenuApplication {

	public static void main(String[] args) {
//		System.setProperty("apollo.configService", "http://10.124.0.99:8080");
		SpringApplication.run(MenuApplication.class, args);
	}

}
