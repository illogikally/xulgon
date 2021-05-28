package me.min.xulgon;

import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.model.Comment;
import me.min.xulgon.repository.CommentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@SpringBootApplication
public class XulgonApplication {

	public static void main(String[] args) {
		SpringApplication.run(XulgonApplication.class, args);
	}
}
