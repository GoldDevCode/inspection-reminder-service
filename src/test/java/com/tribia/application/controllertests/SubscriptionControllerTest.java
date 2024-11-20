package com.tribia.application.controllertests;

import com.tribia.application.controller.SubscriptionController;
import com.tribia.application.dto.UserDto;
import com.tribia.application.dto.UserSubscriptionRequest;
import com.tribia.application.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @Test
    void subscribeUser_shouldReturnUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("test@example.com")
                .build();

        Mockito.when(subscriptionService.subscribeUser(any(UserSubscriptionRequest.class)))
                .thenReturn(userDto);

        String requestBody = "{\"email\":\"test@example.com\",\"licensePlates\":[\"EB11111\",\"EB22222\"]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void unsubscribeVehicles_shouldReturnUserDto() throws Exception {
        UserDto userDto = UserDto
                .builder()
                .email("test@example.com")
                .build();

        Mockito.when(subscriptionService.unsubscribeVehicles(anyString(), any(List.class)))
                .thenReturn(userDto);

        String requestBody = "{\"email\":\"test@example.com\",\"licensePlates\":[\"EB11111\",\"EB22222\"]}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/unsubscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}
