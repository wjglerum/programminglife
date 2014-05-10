package webapplication;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Application {

	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultiPartConfigFactory factory = new MultiPartConfigFactory();
		factory.setMaxFileSize("128KB");
		factory.setMaxRequestSize("128KB");
		//TODO: uitzoeken wat de echte max filesize is
		return factory.createMultipartConfig();
	}
	
    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Application.class, args);
    }

}
