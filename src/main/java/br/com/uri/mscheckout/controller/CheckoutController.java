package br.com.uri.mscheckout.controller;

import br.com.uri.mscheckout.controller.request.CheckoutRequest;
import br.com.uri.mscheckout.service.Checkout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


@RestController
public class CheckoutController {

    private final Checkout checkout;

    public CheckoutController(Checkout checkout) {
        this.checkout = checkout;
    }

    @PostMapping("/v1/checkout")
    public ResponseEntity<?> checkout(@RequestBody @Valid CheckoutRequest checkoutRequest) {
        checkout.doCheckout(checkoutRequest);
        return ResponseEntity.ok("OK");
    }


    @GetMapping("/v1/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.internalServerError().body("pong");
    }
}
