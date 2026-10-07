package com.example.ejadwebapplication.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class UltraMsgConfig {

    @Value("${ultramsg.instance.id}")
    private String instanceId;

    // اسم الميثود = اسم الـ Bean = اسم الحقل في WhatsAppSender
    // الـ token ما ينحط هنا كـ header لأن UltraMsg ياخذه داخل الـ form نفسه
    @Bean
    public RestClient ultraMsgRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.ultramsg.com/" + instanceId)
                .build();
    }
}
