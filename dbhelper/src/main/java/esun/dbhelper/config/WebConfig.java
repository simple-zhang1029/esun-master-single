//package esun.dbhelper.config;
//
//
//import esun.dbhelper.interceptor.Interceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * web相关设置
// * @author xiaoliebin
// */
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Autowired
//    private Interceptor interceptor;
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //设置拦截器，拦截所有请求
//        registry.addInterceptor(interceptor).addPathPatterns("/**");
//    }
//
//
//}
