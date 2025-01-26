package es.board.config.jwt;

import es.board.interceptor.IpLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private IpLimitInterceptor sessionLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionLimitInterceptor)
                .addPathPatterns("/")  // 모든 경로에 적용
                .excludePathPatterns("/public/**");  // 특정 경로 제외
    }
}