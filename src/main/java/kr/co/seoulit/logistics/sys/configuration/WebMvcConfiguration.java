package kr.co.seoulit.logistics.sys.configuration;

import java.nio.charset.Charset;

import jakarta.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.seoulit.logistics.sys.interceptor.LoggerInterceptor;
import kr.co.seoulit.logistics.sys.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	private final long MAX_AGE_SEC = 3600;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOriginPatterns("http://localhost:3000")
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*")
				.maxAge(MAX_AGE_SEC);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor())
				.addPathPatterns("/")
				.addPathPatterns("/*")
				.excludePathPatterns("/*logout*")
				.excludePathPatterns("/*login*")
				.excludePathPatterns("/error");

		registry.addInterceptor(new LoggerInterceptor());

		WebMvcConfigurer.super.addInterceptors(registry);
	}

	// 2개의 빈은 인코딩 관련.
	@Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		return (Filter) characterEncodingFilter;
	}

	@Bean
	public HttpMessageConverter<String> responseBodyConverter() {
		return new StringHttpMessageConverter(Charset.forName("UTF-8"));
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
}
