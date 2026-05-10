package br.com.uri.mscheckout.service;

import br.com.uri.mscheckout.client.domain.ProductResult;
import br.com.uri.mscheckout.controller.request.CheckoutRequest;
import br.com.uri.mscheckout.entities.CheckoutEntity;
import br.com.uri.mscheckout.repository.CheckoutRepository;

import java.math.BigDecimal;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CheckoutImpl implements Checkout {

    private final ProductService productService;

    private final CheckoutRepository checkoutRepository;

    public CheckoutImpl(ProductService productService, CheckoutRepository checkoutRepository) {
        this.productService = productService;
        this.checkoutRepository = checkoutRepository;
    }

    @Override
    public void doCheckout(CheckoutRequest checkoutRequest) {
        var product = productService.get(checkoutRequest.getProductRequest());
        checkoutRepository.save(createEntity(checkoutRequest.getOrderId(), product));
    }

    private @NonNull CheckoutEntity createEntity(String orderId, ProductResult result) {
        var entity = new CheckoutEntity();
        entity.setProductId(result.getBody().getProductId());
        entity.setOrderId(orderId);
        entity.setPrice(BigDecimal.valueOf(result.getBody().getSalesPrice()));
        return entity;
    }

}
