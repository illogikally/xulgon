package me.min.xulgon;

import com.sirv.SirvClientImpl;
import com.sirv.spring.RestTemplateAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableSpringConfigured
public class XulgonApplication {

	public static void main(String[] args) {
		SpringApplication.run(XulgonApplication.class, args);
	}

	@Bean
	public SirvClientImpl getSirvClient(@Value("${app.sirv-id}") String clientId,
													@Value("${app.sirv-secret}") String clientSecret) {
		return new SirvClientImpl(clientId, clientSecret, new RestTemplateAdapter());
	}
}
