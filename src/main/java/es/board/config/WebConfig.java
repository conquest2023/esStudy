package es.board.config;

import es.board.filter.interceptor.IpLimitInterceptor;
import es.board.filter.interceptor.WebVisitorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private  WebVisitorInterceptor webVisitorInterceptor;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("/workly-info.jpeg")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173","https://workly.info")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedMethods("*")
                .allowCredentials(true);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webVisitorInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/sse/**", "/api/notifications/subscribe")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

    }
}