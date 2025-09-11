package com.matsim.service;

import com.matsim.util.LoginInterceptor;
import org.jboss.logging.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by MingLU on 2018/5/18,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    public final static String SESSION_KEY = "user";
    public final static Logger log = Logger.getLogger(WebConfig.class);
    private ApplicationContext applicationContext;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

//        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
//        log.info("Path '/static/' is added to resource handler.");
//        registry.addResourceHandler("/templates/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/templates/");
//        log.info("Path '/templates/' is added to resource handler.");

//        registry.addResourceHandler(registry);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String interceptor = "/static/console.html";
        log.info("Path '" + interceptor + "' is intercepted");
        //拦截规则：除了login，其他都拦截判断
//        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**").excludePathPatterns("/verify/**").excludePathPatterns("/static/login/**");
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns(interceptor);
//        super.addInterceptors(registry);
    }

}