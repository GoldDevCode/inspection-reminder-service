package com.tribia.application.clienttests;

import com.tribia.application.client.SVVApiResponseHandler;
import com.tribia.application.dto.svv.response.KjoretoyData;
import com.tribia.application.dto.svv.response.PeriodiskKjoretoyKontroll;
import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SVVApiResponseHandlerTest {

    private SVVApiResponseHandler responseHandler;

    @BeforeEach
    void setUp() {
        responseHandler = new SVVApiResponseHandler();
    }

    @Test
    void handleResponse_shouldReturnValidResponse() {
        // Arrange
        SVVApiResponse expectedResponse = new SVVApiResponse();
        KjoretoyData kjoretoyData = new KjoretoyData();
        PeriodiskKjoretoyKontroll periodiskKjoretoyKontroll = new PeriodiskKjoretoyKontroll();
        periodiskKjoretoyKontroll.setKontrollfrist("2025-01-31");
        periodiskKjoretoyKontroll.setSistGodkjent("2023-07-31");
        kjoretoyData.setPeriodiskKjoretoyKontroll(periodiskKjoretoyKontroll);
        expectedResponse.setKjoretoydataListe(List.of(kjoretoyData));

        ResponseEntity<SVVApiResponse> responseEntity = ResponseEntity.ok(expectedResponse);

        // Act
        SVVApiResponse result = responseHandler.handleResponse(() -> responseEntity);

        // Assert
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void handleResponse_shouldThrowApiExceptionWhenBodyIsNull() {
        // Arrange
        ResponseEntity<SVVApiResponse> responseEntity = ResponseEntity.ok().build();

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> responseEntity))
                .isInstanceOf(ApiException.class)
                .hasMessage("Response body is null");
    }

    @Test
    void handleResponse_shouldThrowInformationNotAvailableException() {
        // Arrange
        SVVApiResponse response = new SVVApiResponse();
        response.setFeilmelding("OPPLYSNINGER_IKKE_TILGJENGELIGE");
        ResponseEntity<SVVApiResponse> responseEntity = ResponseEntity.ok(response);

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> responseEntity))
                .isInstanceOf(SVVInformationNotAvailableException.class)
                .hasMessage("Information not available for the given license plate");
    }

    @Test
    void handleResponse_shouldThrowBadRequestException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> {
            throw exception;
        }))
                .isInstanceOf(SVVBadRequestException.class)
                .hasMessage("More than one field filled in the request");
    }

    @Test
    void handleResponse_shouldThrowForbiddenException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.FORBIDDEN);

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> {
            throw exception;
        }))
                .isInstanceOf(SVVForbiddenException.class)
                .hasMessage("API key does not exist, is not active, or user is blocked");
    }

    @Test
    void handleResponse_shouldThrowQuotaExceededException() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> {
            throw exception;
        }))
                .isInstanceOf(SVVRequestQuotaExceededException.class)
                .hasMessage("User has exceeded the quota for the current period");
    }

    @Test
    void handleResponse_shouldThrowApiExceptionForOtherClientErrors() {
        // Arrange
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> {
            throw exception;
        }))
                .isInstanceOf(ApiException.class)
                .hasMessage("Client error occurred while fetching vehicle data from SVV API");
    }

    @Test
    void handleResponse_shouldThrowApiExceptionForServerErrors() {
        // Arrange
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> {
            throw exception;
        }))
                .isInstanceOf(ApiException.class)
                .hasMessage("Server error occurred while fetching vehicle data from SVV API");
    }

    @Test
    void handleResponse_shouldThrowApiExceptionForRestClientException() {
        // Arrange
        RestClientException exception = new RestClientException("Test exception");

        // Act & Assert
        assertThatThrownBy(() -> responseHandler.handleResponse(() -> {
            throw exception;
        }))
                .isInstanceOf(ApiException.class)
                .hasMessage("Failed to fetch vehicle data from SVV API");
    }
}
