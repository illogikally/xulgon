package me.min.xulgon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.MessageFormat;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

   @Value("${resource.path}")
   private String resourcePath;

   @Override
   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      String path = MessageFormat.format("file:/{0}/", resourcePath);
      registry.addResourceHandler("/contents/**")
              .addResourceLocations(path);

//      registry.addResourceHandler("/contents/**")
//            .addResourceLocations("file:/usr/local/Storage/");

      registry.addResourceHandler("/swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");

      registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/");
   }
}
