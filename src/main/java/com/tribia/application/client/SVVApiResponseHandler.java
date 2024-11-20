package com.tribia.application.client;

import com.tribia.application.dto.svv.response.SVVApiResponse;
import com.tribia.application.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class SVVApiResponseHandler {

    Logger logger = LoggerFactory.getLogger(SVVApiResponseHandler.class);

    public SVVApiResponse handleResponse(Supplier<ResponseEntity<SVVApiResponse>> responseSupplier) {
        try {
            ResponseEntity<SVVApiResponse> response = responseSupplier.get();
            return handleSuccessResponse(response);
        } catch (HttpClientErrorException e) {
            handleHttpClientErrorException(e);
        } catch (HttpServerErrorException e) {
            handleHttpServerErrorException(e);
        } catch (RestClientException e) {
            handleRestClientException(e);
        }
        throw new ApiException("Unexpected error occurred", new RuntimeException("Unexpected error occurred"));
    }

    private SVVApiResponse handleSuccessResponse(ResponseEntity<SVVApiResponse> response) {
        if (response.getBody() == null) {
            throw new ApiException("Response body is null", new RuntimeException("Response body is null"));
        }
        if ("OPPLYSNINGER_IKKE_TILGJENGELIGE".equals(response.getBody().getFeilmelding())) {
            throw new SVVInformationNotAvailableException("Information not available for the given license plate");
        }
        if (response.getBody().getKjoretoydataListe() == null || response.getBody().getKjoretoydataListe().isEmpty()) {
            throw new SVVInformationNotAvailableException("Information not available for the given license plate.Feilmelding: " + response.getStatusCode());
        }
        return response.getBody();
    }

    private void handleHttpClientErrorException(HttpClientErrorException e) {
        switch (e.getStatusCode()) {
            case BAD_REQUEST:
                logger.warn("Bad request: More than one field filled in the request");
                throw new SVVBadRequestException("More than one field filled in the request", e);
            case FORBIDDEN:
                logger.error("Forbidden: API key does not exist, is not active, or user is blocked");
                throw new SVVForbiddenException("API key does not exist, is not active, or user is blocked", e);
            case TOO_MANY_REQUESTS:
                logger.warn("Too many requests: User has exceeded the quota for the current period");
                throw new SVVRequestQuotaExceededException("User has exceeded the quota for the current period", e);
            default:
                logger.error("Client error occurred: {}", e.getMessage());
                throw new ApiException("Client error occurred while fetching vehicle data from SVV API", e);
        }
    }

    private void handleHttpServerErrorException(HttpServerErrorException e) {
        logger.error("Server error occurred while fetching vehicle data: {}", e.getMessage());
        throw new ApiException("Server error occurred while fetching vehicle data from SVV API", e);
    }

    private void handleRestClientException(RestClientException e) {
        logger.error("Error occurred while fetching vehicle data: {}", e.getMessage());
        throw new ApiException("Failed to fetch vehicle data from SVV API", e);
    }
}

