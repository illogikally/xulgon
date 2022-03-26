package me.min.xulgon;

import com.sirv.SirvClientImpl;
import com.sirv.spring.RestTemplateAdapter;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.Photo;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.util.Util;
import net.coobird.thumbnailator.builders.BufferedImageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.util.Pair;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;

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

	@Bean
	@Autowired
	public ApplicationRunner run(PhotoRepository photoRepository, PageRepository pageRepository) {
		return args -> {
			pageRepository.findAll()
					.stream()
					.map(Page::getCoverPhoto).filter(Objects::nonNull)
					.forEach(photo -> {
						try {
							URL url = new URL("https://xulgon.sirv.com/" + photo.getName());
							BufferedImage image = ImageIO.read(url);
							photo.setDominantColorLeft(Util.getDominantColorLeft(image));
							photo.setDominantColorRight(Util.getDominantColorRight(image));
							photoRepository.save(photo);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
		};
	};
}
