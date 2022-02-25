package me.min.xulgon;

import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.model.Comment;
import me.min.xulgon.repository.CommentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import java.time.Instant;

@SpringBootApplication
@EnableSpringConfigured
public class XulgonApplication {

	public static void main(String[] args) {
		SpringApplication.run(XulgonApplication.class, args);
	}
}
