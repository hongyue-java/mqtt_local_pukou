package mqtt_receive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// Spring Boot 应用的标识
@SpringBootApplication
public class ReceiveApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ReceiveApplication.class, args);
    }

    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}