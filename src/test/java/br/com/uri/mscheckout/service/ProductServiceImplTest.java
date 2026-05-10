package br.com.uri.mscheckout.service;

import br.com.uri.mscheckout.client.ProductClient;
import br.com.uri.mscheckout.client.domain.Product;
import br.com.uri.mscheckout.client.domain.ProductResult;
import br.com.uri.mscheckout.controller.request.ProductRequest;
import br.com.uri.mscheckout.exception.ProductNotValidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setProductId("prod-1");
        productRequest.setSalesPrice(99.99);
        productRequest.setName("Test Product");
    }

    @Test
    void get_shouldReturnProductResult_whenProductIsInStock() {
        var product = inStockProduct();
        var productResult = resultWith(product);
        when(productClient.validate(any())).thenReturn(ResponseEntity.ok(productResult));

        var result = productService.get(productRequest);

        assertThat(result).isEqualTo(productResult);
    }

    @Test
    void get_shouldMapRequestFieldsToProduct() {
        when(productClient.validate(any())).thenReturn(ResponseEntity.ok(resultWith(inStockProduct())));

        productService.get(productRequest);

        verify(productClient).validate(argThat(p ->
                "prod-1".equals(p.getProductId()) &&
                99.99 == p.getSalesPrice() &&
                "Test Product".equals(p.getName())
        ));
    }

    @Test
    void get_shouldThrow_whenResponseIsNot2xx() {
        when(productClient.validate(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

        assertThatThrownBy(() -> productService.get(productRequest))
                .isInstanceOf(ProductNotValidException.class)
                .hasMessageContaining("Failed to validate product with external service");
    }

    @Test
    void get_shouldThrow_whenProductIsOutOfStock() {
        var product = new Product();
        product.setStatus("out of stock");
        when(productClient.validate(any())).thenReturn(ResponseEntity.ok(resultWith(product)));

        assertThatThrownBy(() -> productService.get(productRequest))
                .isInstanceOf(ProductNotValidException.class)
                .hasMessageContaining("Product out of stock");
    }

    @Test
    void get_shouldThrow_whenResponseBodyIsNull() {
        when(productClient.validate(any())).thenReturn(ResponseEntity.ok(null));

        assertThatThrownBy(() -> productService.get(productRequest))
                .isInstanceOf(ProductNotValidException.class)
                .hasMessageContaining("Product out of stock");
    }

    @Test
    void get_shouldThrow_whenProductBodyIsNull() {
        var productResult = new ProductResult();
        productResult.setBody(null);
        when(productClient.validate(any())).thenReturn(ResponseEntity.ok(productResult));

        assertThatThrownBy(() -> productService.get(productRequest))
                .isInstanceOf(ProductNotValidException.class)
                .hasMessageContaining("Product out of stock");
    }

    private Product inStockProduct() {
        var product = new Product();
        product.setProductId("prod-1");
        product.setSalesPrice(99.99);
        product.setStatus("in stock");
        return product;
    }

    private ProductResult resultWith(Product product) {
        var result = new ProductResult();
        result.setBody(product);
        return result;
    }

}
