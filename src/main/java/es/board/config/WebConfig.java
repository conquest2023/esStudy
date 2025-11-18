package es.board.config;

import es.board.interceptor.IpLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private IpLimitInterceptor sessionLimitInterceptor;

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
        registry.addInterceptor(sessionLimitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

    }
}