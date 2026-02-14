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

        registry.addResourceHandler("/workly-info.jpeg")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        // 2. 파비콘이나 루트에 있는 jpeg 이미지 처리
        registry.addResourceHandler("/*.jpeg", "/*.ico")
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
                .excludePathPatterns("/post/**", "/assets/**", "/**/*.js", "/**/*.css", "/*.jpeg", "/*.ico", "/error")
                .excludePathPatterns("/sse/**", "/api/notifications/subscribe");

    }
}