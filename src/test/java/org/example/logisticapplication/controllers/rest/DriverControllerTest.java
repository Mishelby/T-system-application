package org.example.logisticapplication.controllers.rest;

import org.example.logisticapplication.domain.City.CityEntity;
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

@AutoConfigureMockMvc
@SpringBootTest
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldSuccessCreateDriver() throws Exception {
        var driverEntity = new DriverEntity(
                null,
                "Name",
                "Second Name",
                8947609245L,
                0,
                DriverStatus.REST.name(),
                createCityEntity(),
                null
        );

        var driverJson = mapper.writeValueAsString(driverEntity);

        var createdDriverJson = mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(driverJson))
                .andExpect(status().is(201))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var driverResponse = mapper.readValue(createdDriverJson, DriverEntity.class);

        Assertions.assertNotNull(driverResponse.getId());
        Assertions.assertEquals(driverEntity.getName(), driverResponse.getName());
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