package br.com.uri.mscheckout.service;

import br.com.uri.mscheckout.client.ProductClient;
import br.com.uri.mscheckout.client.domain.Product;
import br.com.uri.mscheckout.client.domain.ProductResult;
import br.com.uri.mscheckout.controller.request.ProductRequest;
import br.com.uri.mscheckout.exception.ProductNotValidException;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductClient productClient;

    public ProductServiceImpl(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public ProductResult get(ProductRequest productRequest) {
        Product product = new Product();
        product.setProductId(productRequest.getProductId());
        product.setSalesPrice(productRequest.getSalesPrice());
        product.setName(productRequest.getName());

        ResponseEntity<ProductResult> response = productClient.validate(product);
        validateResponse(response);
        return response.getBody();
    }

    private void validateResponse(ResponseEntity<ProductResult> response) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ProductNotValidException("Failed to validate product with external service");
        }

        boolean inStock = Optional.ofNullable(response.getBody())
                .map(ProductResult::getBody)
                .map(product -> "in stock".equalsIgnoreCase(product.getStatus()))
                .orElse(false);

        if (!inStock) {
            throw new ProductNotValidException("Product out of stock");
        }
    }

}
