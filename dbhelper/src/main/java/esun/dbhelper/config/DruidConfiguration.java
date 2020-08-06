package esun.dbhelper.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class DruidConfiguration {


    @Bean
    public ServletRegistrationBean statViewServlet(){
        //创建servlet注册实体
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
        //设置ip白名单
        servletRegistrationBean.addInitParameter("allow","127.0.0.1");
        //设置ip黑名单，如果allow与deny共同存在时,deny优先于allow
        servletRegistrationBean.addInitParameter("deny","192.168.0.19");
        //设置控制台管理用户
        servletRegistrationBean.addInitParameter("loginUsername","druid");
        servletRegistrationBean.addInitParameter("loginPassword","123456");
        //是否可以重置数据
        servletRegistrationBean.addInitParameter("resetEnable","false");

        return servletRegistrationBean;
    }

    //下面的配置解决批量操作的异常
    @Bean(name = "wallConfig")
    WallConfig wallFilterConfig(){
        WallConfig wallConfig = new WallConfig();
        //允许一次执行多条语句
        wallConfig.setMultiStatementAllow(true);
        wallConfig.setNoneBaseStatementAllow(true);
        wallConfig.setStrictSyntaxCheck(false);
        return wallConfig;
    }

    @Bean(name = "wallFilter")
    @DependsOn("wallConfig")
    WallFilter wallFilter(WallConfig wallConfig){
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig);
        return wallFilter;
    }
//    @Bean
//    public FilterRegistrationBean statFilter(){
//        //创建过滤器
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
//        //设置过滤器过滤路径
//        filterRegistrationBean.addUrlPatterns("/*");
//        //忽略过滤的形式
//        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }
}
