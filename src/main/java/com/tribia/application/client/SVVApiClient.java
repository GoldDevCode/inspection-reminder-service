package com.tribia.application.client;


import com.tribia.application.dto.svv.response.SVVApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SVVApiClient {

    private final RestTemplate restTemplate;
    private final SVVApiResponseHandler responseHandler;
    private final SVVApiRequestBuilder requestBuilder;

    @Value("${svv.api.uri}")
    private String svvApiUri;

    public SVVApiResponse getVehicleData(String licensePlate) {
        HttpEntity<String> requestEntity = requestBuilder.buildRequestEntity();
        String url = svvApiUri + licensePlate;

        return responseHandler.handleResponse(() ->
                restTemplate.exchange(url, HttpMethod.GET, requestEntity, SVVApiResponse.class)
        );
    }
}
