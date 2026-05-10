package br.com.uri.mscheckout.controller.request;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductRequest {

    @NotNull
    @NotBlank
    private String productId;

    @Min(value = 0, message = "O valor não pode ser 0 ou negativo")
    private Double salesPrice;

    @NotNull
    @NotBlank
    private String name;




}
