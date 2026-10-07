package com.example.ejadwebapplication.Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

// نفس فكرة EmailSender: Component مو Service، و @Async على الميثودات العامة بس
// نستقبل String مو entity عشان الـ thread الثاني ما يلمس علاقات lazy
@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppSender {

    private final RestClient ultraMsgRestClient;

    // مو final عشان ما يدخل في الـ constructor حق Lombok
    @Value("${ultramsg.token}")
    private String token;

    // لصاحب بلاغ الـ LOST: لقينا غرض ممكن يكون غرضه
    @Async
    public void sendMatchFound(String phone, String fullName, String lostTitle, String foundTitle) {
        send(phone, "مرحباً " + fullName + "،\n\n"
                + "لقينا غرض ممكن يكون هو غرضك المفقود \"" + lostTitle + "\": " + foundTitle + ".\n"
                + "ادخل على إيجاد وراجع التطابق عشان تأكده.");
    }

    // private وبدون @Async: تنادى من داخل الكلاس (self-invocation)
    private void send(String phone, String message) {
        // UltraMsg ياخذ form-urlencoded مو JSON، والـ token داخل الـ form
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("token", token);
        form.add("to", toInternational(phone));
        form.add("body", message);

        try {
            Map<?, ?> response = ultraMsgRestClient.post()
                    .uri("/messages/chat")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(Map.class);

            // ممكن يرجّع 200 والخطأ داخل الرد (مثل token غلط)، فنتحقق منه
            if (response != null && response.get("error") != null) {
                log.warn("WhatsApp message failed: {}", response.get("error"));
            }
        } catch (RestClientException e) {
            // فشل الواتساب ما يفشّل العملية، نفس فلسفة الإيميل والـ AI
            log.warn("WhatsApp request failed: {}", e.getClass().getSimpleName());
        }
    }

    // الأرقام عندنا محفوظة 05XXXXXXXX، والواتساب يبيها بالصيغة الدولية +9665XXXXXXXX
    private String toInternational(String phone) {
        return "+966" + phone.substring(1);
    }
}
