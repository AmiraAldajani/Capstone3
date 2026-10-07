package com.example.ejadwebapplication.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GoogleMapsConfig {

    // اسم الميثود = اسم الـ Bean = اسم الحقل في GoogleMapsService
    // المفتاح ما ينحط هنا كـ header لأن Geocoding API ياخذه في الرابط (key=...)
    @Bean
    public RestClient googleMapsRestClient() {
        return RestClient.builder()
                .baseUrl("https://maps.googleapis.com/maps/api")
                .build();
    }
}
