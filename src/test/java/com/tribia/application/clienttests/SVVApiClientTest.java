package com.tribia.application.clienttests;

import com.tribia.application.client.SVVApiClient;
import com.tribia.application.client.SVVApiRequestBuilder;
import com.tribia.application.client.SVVApiResponseHandler;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SVVApiClientTest {

    private static final String API_URI = "https://api.example.com/";
    private static final String LICENSE_PLATE = "AB12345";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private SVVApiResponseHandler responseHandler;
    @Mock
    private SVVApiRequestBuilder requestBuilder;
    @InjectMocks
    private SVVApiClient svvApiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(svvApiClient, "svvApiUri", API_URI);
    }

    @Test
    void getVehicleData_shouldReturnValidResponse() {
        // Arrange
        HttpEntity<String> mockEntity = new HttpEntity<>("");
        when(requestBuilder.buildRequestEntity()).thenReturn(mockEntity);

        SVVApiResponse expectedResponse = new SVVApiResponse();
        when(responseHandler.handleResponse(any())).thenReturn(expectedResponse);

        // Act
        SVVApiResponse result = svvApiClient.getVehicleData(LICENSE_PLATE);

        // Assert
        assert result != null;
    }
}

