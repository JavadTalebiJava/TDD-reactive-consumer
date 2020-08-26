package com.example.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureWireMock(port = 8080)
@AutoConfigureJson
public class ReservationClientTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationClient client;

    @Test
    public void getAllReservations() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(
                Arrays.asList(
                        new Reservation("1", "A"), new Reservation("2", "B"),
                        new Reservation("3", "C"), new Reservation("4", "D"))
        );

        //Producer Microservis'i Mock ediyoruz.(Fake in olusturuyoruz)
        WireMock
                .stubFor(WireMock.get(WireMock.urlEqualTo("/reservations"))
                        .willReturn(WireMock
                                .aResponse()
                                .withBody(json)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(200)
                        )
                );
        Flux<Reservation> reservationFlux = this.client.getAllReservations();
        List<String> names = Arrays.asList("A", "B", "C", "D");
        StepVerifier
                .create(reservationFlux)
                .expectNextMatches(res -> names.contains(res.getName()))
                .expectNextMatches(res -> names.contains(res.getName()))
                .expectNextMatches(res -> names.contains(res.getName()))
                .expectNextMatches(res -> names.contains(res.getName()))
                .verifyComplete();
    }
}
