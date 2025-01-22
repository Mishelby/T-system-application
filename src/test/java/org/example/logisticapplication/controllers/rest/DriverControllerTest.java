package org.example.logisticapplication.controllers.rest;

import lombok.extern.slf4j.Slf4j;
import org.example.logisticapplication.domain.City.CityEntity;
import org.example.logisticapplication.domain.Driver.Driver;
import org.example.logisticapplication.domain.Driver.DriverEntity;
import org.example.logisticapplication.domain.Driver.DriverStatus;
import org.example.logisticapplication.domain.Truck.TruckEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.example.logisticapplication.domain.Truck.TruckStatus.SERVICEABLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Test case to verify successful creation of a driver.
     *
     * @throws Exception if an error occurs during JSON processing or request execution.
     */
    @Test
    void shouldSuccessCreateDriver() throws Exception {
        // Create a new Driver object with sample data
        var driver = new Driver(
                null,
                "John",
                "Doe",
                657853219L,
                0,
                DriverStatus.fromDisplayName("REST").getDisplayName(),
                1L,
                null
        );

        // Convert the Driver object to JSON
        var driverJson = mapper.writeValueAsString(driver);

        // Perform POST request to create a new driver and expect HTTP 200 status
        var createdDriverJson = mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Parse the response JSON to DriverEntity object
        var driverResponse = mapper.readValue(createdDriverJson, DriverEntity.class);

        // Validate that the ID is generated and the name matches the input
        Assertions.assertNotNull(driverResponse.getId());
        Assertions.assertEquals(driver.name(), driverResponse.getName());
    }

    @Test
    void shouldSuccessCreateTruck() throws Exception {
        var truckEntity = new TruckEntity(
                null,
                "F1843",
                0,
                SERVICEABLE.name(),
                2000.0,
                createCityEntity()
        );

        var truckJson = mapper.writeValueAsString(truckEntity);

        var truckResponse = mockMvc.perform(post("/api/trucks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(truckJson))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var responseTruck = mapper.readValue(truckResponse, TruckEntity.class);

        Assertions.assertNotNull(responseTruck.getId());
        Assertions.assertEquals(truckEntity.getRegistrationNumber(), responseTruck.getRegistrationNumber());

    }

    @Test
    void getDriverById() {
    }

    private CityEntity createCityEntity() {
        return new CityEntity(
                1L,
                "Moscow",
                null
        );
    }
}