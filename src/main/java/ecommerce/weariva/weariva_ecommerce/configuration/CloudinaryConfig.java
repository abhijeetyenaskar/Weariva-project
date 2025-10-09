package ecommerce.weariva.weariva_ecommerce.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_url}")
    private String cloudinary_url;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinary_url);
    }
}
