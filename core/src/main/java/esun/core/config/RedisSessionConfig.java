package esun.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * redis session配置类
 */
//@Configuration
//maxInactiveIntervalInSeconds设置默认过期时间,不设置默认30分钟
@EnableRedisHttpSession(maxInactiveIntervalInSeconds=60*60)
public class RedisSessionConfig {

}
