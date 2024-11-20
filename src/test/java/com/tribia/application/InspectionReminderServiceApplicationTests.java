package com.tribia.application;

import com.tribia.application.dto.ApiResponse;
import com.tribia.application.dto.SubscriptionRemovalRequest;
import com.tribia.application.dto.UserSubscriptionRequest;
import com.tribia.application.service.UserService;
import com.tribia.application.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InspectionReminderServiceApplicationTests {

    private final static String BASE_URI_SUBSCRIBE = "/api/subscribe";
    private final static String BASE_URI_UNSUBSCRIBE = "/api/unsubscribe";
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    VehicleService vehicleService;
    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void testSubscribeUserEndpoint_ShouldReturn_OK_Response() {

        UserSubscriptionRequest request = new UserSubscriptionRequest();
        request.setEmail("test@example.com");
        request.setLicensePlates(List.of("EB12345", "EB23456"));

        // Initially assert that there is no user with the email
        var savedUserBefore = userService
                .findUserByEmail(request.getEmail());

        assert savedUserBefore.isEmpty();

        // Initially assert that there are no vehicles with the license plates
        var savedVehiclesBefore = vehicleService
                .findByLicensePlates(request.getLicensePlates());

        assert savedVehiclesBefore.isEmpty();

        var response = restTemplate
                .postForEntity(BASE_URI_SUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().isSuccess();
        assert response.getBody().getMessage().equals("Api Call Successful");
        assert response.getBody().getData() != null;

        // Assertions on data actually saved in the database
        var savedUserAfter = userService
                .findUserByEmail(request.getEmail());

        assert savedUserAfter.isPresent();

        assertThat(savedUserAfter.get()
                .getEmail())
                .isEqualTo(request.getEmail());

        //Use assertThat to validate the vehicles are saved in the database
        var savedVehiclesAfter = vehicleService
                .findByLicensePlates(request.getLicensePlates());

        // saved vehicle has same user email
        savedVehiclesAfter.forEach(vehicle ->
                assertThat(vehicle.getUser()
                        .getEmail())
                        .isEqualTo(request.getEmail())
        );

        assertThat(savedVehiclesAfter.size())
                .isEqualTo(request.getLicensePlates().size());

        savedVehiclesAfter.forEach(vehicle ->
                assertThat(request.getLicensePlates()
                        .contains(vehicle.getLicensePlate()))
                        .isTrue());

        // check if the user is subscribed to the vehicles
        savedVehiclesAfter.forEach(vehicle ->
                assertThat(vehicle.isSubscribed())
                        .isTrue());
    }

    @Test
    void testSubscribeUserEndpoint_ShouldReturn_OK_For_New_Vehicle_With_User_Already_Subscribed_For_Two_Vehicles() {

        UserSubscriptionRequest request = new UserSubscriptionRequest();
        request.setEmail("abc@gmail.com");
        request.setLicensePlates(List.of("EB88888"));
        // Initially verify that there are 2 vehicles present in the database for this user

        var savedVehiclesBefore = vehicleService
                .findByLicensePlates(List.of("EB11111", "EB22222"));

        assertThat(savedVehiclesBefore.size())
                .isEqualTo(2);

        //verify both vehicles have the same user email
        savedVehiclesBefore.forEach(vehicle ->
                assertThat(vehicle.getUser().getEmail())
                        .isEqualTo(request.getEmail()));

        //verify that the user is subscribed to the vehicles
        savedVehiclesBefore.forEach(vehicle ->
                assertThat(vehicle.isSubscribed())
                        .isTrue());

        var response = restTemplate
                .postForEntity(BASE_URI_SUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().isSuccess();
        assert response.getBody().getMessage().equals("Api Call Successful");
        assert response.getBody().getData() != null;

        //Use assertThat to validate the user exists in the database
        var savedUserAfter = userService
                .findUserByEmail(request.getEmail());

        assert savedUserAfter.isPresent();
        assert savedUserAfter.get()
                .getEmail()
                .equals(request.getEmail());

        //Use assertThat to validate the vehicles are saved in the database
        var licensePlates = List.of("EB11111", "EB22222", "EB88888");
        var savedVehicleAfter = vehicleService
                .findByLicensePlates(licensePlates);


        // saved vehicle has same user email
        savedVehicleAfter.forEach(vehicle ->
                assertThat(vehicle.getUser()
                        .getEmail())
                        .isEqualTo(request.getEmail()));

        assertThat(savedVehicleAfter.size())
                .isEqualTo(3);

        savedVehicleAfter.forEach(vehicle ->
                assertThat(licensePlates
                        .contains(vehicle.getLicensePlate()))
                        .isTrue());

        // check if the user is subscribed to the vehicles
        savedVehicleAfter.forEach(vehicle ->
                assertThat(vehicle.isSubscribed())
                        .isTrue());
    }

    @Test
    void testSubscribeUserEndpoint_ShouldReturn_Already_Subscribed_To_Same_User() {

        UserSubscriptionRequest request = new UserSubscriptionRequest();
        request.setEmail("abc@gmail.com");
        request.setLicensePlates(List.of("EB11111", "EB22222"));

        var response = restTemplate
                .postForEntity(BASE_URI_SUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is4xxClientError();
        assert response.getBody() != null;
        assert !response.getBody().isSuccess();
        assert response.getBody()
                .getMessage()
                .equals("User is already subscribed to all provided license plates.");
    }

    @Test
    void testSubscribeUserEndpoint_ShouldReturn_Already_Subscribed_To_Some_Other_User() {

        UserSubscriptionRequest request = new UserSubscriptionRequest();
        request.setEmail("abc@gmail.com");
        request.setLicensePlates(List.of("EB33333", "EB44444"));

        // Initially assert that there are 2 vehicles present in the database for this user
        var savedVehiclesBefore = vehicleService
                .findByLicensePlates(List.of("EB11111", "EB22222"));

        assertThat(savedVehiclesBefore.size())
                .isEqualTo(2);

        var response = restTemplate
                .postForEntity(BASE_URI_SUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is4xxClientError();
        assert response.getBody() != null;
        assert !response.getBody().isSuccess();
        assert response.getBody()
                .getMessage()
                .equals("Vehicle with license plate EB33333 is already subscribed by another user.");

        // get the vehicles from database and verify their user email is not the same as the request email
        var savedVehiclesAfter = vehicleService
                .findByLicensePlates(List.of("EB33333", "EB44444"));

        savedVehiclesAfter.forEach(vehicle ->
                assertThat(vehicle.getUser().getEmail())
                        .isNotEqualTo(request.getEmail()));
    }

    @Test
    void testUnsubscribeVehiclesEndpoint_ShouldReturn_OK_Response() {

        SubscriptionRemovalRequest request = new SubscriptionRemovalRequest();
        request.setEmail("xyz@gmail.com");
        request.setLicensePlates(List.of("EB33333", "EB44444"));

        // Initially assert that there are 2 vehicles present in the database for this user
        var savedVehiclesBefore = vehicleService
                .findByLicensePlates(request.getLicensePlates());

        assertThat(savedVehiclesBefore.size())
                .isEqualTo(2);

        // Initially assert that the user is subscribed to the vehicles
        savedVehiclesBefore.forEach(vehicle ->
                assertThat(vehicle.isSubscribed())
                        .isTrue());

        var response = restTemplate
                .postForEntity(BASE_URI_UNSUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().isSuccess();
        assert response.getBody().getMessage().equals("Api Call Successful");
        assert response.getBody().getData() != null;

        //Use assertThat to validate the user exists in the database
        var savedUser = userService
                .findUserByEmail(request.getEmail());

        assert savedUser.isPresent();
        assert savedUser.get()
                .getEmail()
                .equals(request.getEmail());

        var savedVehiclesAfter = vehicleService
                .findByLicensePlates(request.getLicensePlates());

        // saved vehicle has same user email
        savedVehiclesAfter.forEach(vehicle ->
                assertThat(vehicle
                        .getUser()
                        .getEmail())
                        .isEqualTo(request.getEmail()));

        assertThat(savedVehiclesAfter.size())
                .isEqualTo(request.getLicensePlates().size());

        savedVehiclesAfter.forEach(vehicle ->
                assertThat(request
                        .getLicensePlates()
                        .contains(vehicle.getLicensePlate())).isTrue());

        // check if the user is unsubscribed from the vehicles
        savedVehiclesAfter.forEach(vehicle
                -> assertThat(vehicle.isSubscribed()).isFalse());
    }

    @Test
    void testUnsubscribeVehiclesEndpoint_ShouldReturn_Already_Unsubscribed() {

        SubscriptionRemovalRequest request = new SubscriptionRemovalRequest();
        request.setEmail("def@gmail.com");
        request.setLicensePlates(List.of("EB55555"));

        var response = restTemplate
                .postForEntity(BASE_URI_UNSUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is4xxClientError();
        assert response.getBody() != null;
        assert !response.getBody().isSuccess();
        assert response.getBody()
                .getMessage()
                .equals("All provided license plates are already unsubscribed.");
    }

    @Test
    void testUnsubscribeVehiclesEndpoint_ShouldReturn_OK_With_One_Subscribed_And_One_Unsubscribed() {

        SubscriptionRemovalRequest request = new SubscriptionRemovalRequest();
        request.setEmail("def@gmail.com");
        request.setLicensePlates(List.of("EB66666"));

        // Initially assert that there are 2 vehicles with one subscribed and one unsubscribed
        var savedVehicles = vehicleService
                .findByLicensePlates(List.of("EB55555", "EB66666"));

        assertThat(savedVehicles.size())
                .isEqualTo(2);

        savedVehicles.forEach(vehicle -> {
            if (vehicle.getLicensePlate().equals("EB66666")) {
                assertThat(vehicle.isSubscribed()).isTrue();
            } else {
                assertThat(vehicle.isSubscribed()).isFalse();
            }
        });

        var response = restTemplate
                .postForEntity(BASE_URI_UNSUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().isSuccess();
        assert response.getBody().getMessage().equals("Api Call Successful");
        assert response.getBody().getData() != null;

        // assert that now there are 2 vehicles both unsubscribed
        var savedVehiclesAfter = vehicleService
                .findByLicensePlates(List.of("EB55555", "EB66666"));

        assertThat(savedVehiclesAfter.size())
                .isEqualTo(2);

        savedVehiclesAfter.forEach(vehicle ->
                assertThat(vehicle.isSubscribed()).isFalse());
    }

    @Test
    void testUnsubscribeVehiclesEndpoint_ShouldReturn_User_Not_Found() {

        SubscriptionRemovalRequest request = new SubscriptionRemovalRequest();
        request.setEmail("michael@gmail.com");
        request.setLicensePlates(List.of("EB55555"));

        var response = restTemplate
                .postForEntity(BASE_URI_UNSUBSCRIBE, request, ApiResponse.class);

        assert response != null;
        assert response.getStatusCode().is4xxClientError();
        assert response.getBody() != null;
        assert !response.getBody().isSuccess();
        assert response.getBody()
                .getMessage()
                .equals("User not found with email: " + request.getEmail());
    }
}