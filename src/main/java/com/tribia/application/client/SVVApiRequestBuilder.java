package com.tribia.application.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SVVApiRequestBuilder {

    @Value("${svv.api.key}")
    private String apiKey;

    public HttpEntity<String> buildRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("SVV-Authorization", "Apikey " + apiKey);
        return new HttpEntity<>(headers);
    }
}
