package com.example.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor //yasasin lombok, Construtor Injection a gerek kalmadi!
public class ReservationClient {

    private final WebClient webClient;

    public Flux<Reservation> getAllReservations() {
        return webClient
                .get()
                .uri("http://localhost:8080/reservations")
                .retrieve()
                .bodyToFlux(Reservation.class);
    }
}
