package com.tribia.application.clienttests;

import com.tribia.application.client.SVVApiRequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

class SVVApiRequestBuilderTest {

    private static final String API_KEY = "test-api-key";
    private SVVApiRequestBuilder requestBuilder;

    @BeforeEach
    void setUp() {
        requestBuilder = new SVVApiRequestBuilder();
        ReflectionTestUtils.setField(requestBuilder, "apiKey", API_KEY);
    }

    @Test
    void buildRequestEntity_shouldReturnEntityWithCorrectHeaders() {
        // Act
        HttpEntity<String> result = requestBuilder.buildRequestEntity();

        // Assert
        HttpHeaders headers = result.getHeaders();
        assert headers != null;
        assert headers.containsKey("SVV-Authorization");
        assert headers.get("SVV-Authorization").get(0).equals("Apikey " + API_KEY);
    }
}
