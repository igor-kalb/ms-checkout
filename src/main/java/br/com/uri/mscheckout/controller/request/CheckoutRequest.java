package br.com.uri.mscheckout.controller.request;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CheckoutRequest {

    @NotBlank
    private String orderId;

    @NotNull
    @Valid
    private ProductRequest productRequest;

}
