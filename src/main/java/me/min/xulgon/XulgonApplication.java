package me.min.xulgon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

@SpringBootApplication
@EnableSpringConfigured
public class XulgonApplication {

	public static void main(String[] args) {
		SpringApplication.run(XulgonApplication.class, args);
	}

}
