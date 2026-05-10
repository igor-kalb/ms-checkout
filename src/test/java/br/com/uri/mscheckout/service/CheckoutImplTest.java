package br.com.uri.mscheckout.service;

import br.com.uri.mscheckout.client.domain.Product;
import br.com.uri.mscheckout.client.domain.ProductResult;
import br.com.uri.mscheckout.controller.request.CheckoutRequest;
import br.com.uri.mscheckout.controller.request.ProductRequest;
import br.com.uri.mscheckout.entities.CheckoutEntity;
import br.com.uri.mscheckout.exception.ProductNotValidException;
import br.com.uri.mscheckout.repository.CheckoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class CheckoutImplTest {

    @Mock
    private ProductService productService;

    @Mock
    private CheckoutRepository checkoutRepository;

    @InjectMocks
    private CheckoutImpl checkoutImpl;

    private CheckoutRequest checkoutRequest;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest();
        productRequest.setProductId("prod-1");
        productRequest.setSalesPrice(49.99);
        productRequest.setName("Product A");

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setOrderId("order-123");
        checkoutRequest.setProductRequest(productRequest);
    }

    @Test
    void doCheckout_shouldSaveEntityWithCorrectValues() {
        var product = new Product();
        product.setProductId("prod-1");
        product.setSalesPrice(49.99);
        var productResult = new ProductResult();
        productResult.setBody(product);
        when(productService.get(productRequest)).thenReturn(productResult);

        checkoutImpl.doCheckout(checkoutRequest);

        var captor = ArgumentCaptor.forClass(CheckoutEntity.class);
        verify(checkoutRepository).save(captor.capture());
        var saved = captor.getValue();

        assertThat(saved.getOrderId()).isEqualTo("order-123");
        assertThat(saved.getProductId()).isEqualTo("prod-1");
        assertThat(saved.getPrice()).isEqualTo(BigDecimal.valueOf(49.99));
    }

    @Test
    void doCheckout_shouldNotSave_whenProductServiceThrows() {
        doThrow(new ProductNotValidException("Product out of stock"))
                .when(productService).get(any());

        assertThatThrownBy(() -> checkoutImpl.doCheckout(checkoutRequest))
                .isInstanceOf(ProductNotValidException.class)
                .hasMessage("Product out of stock");

        verifyNoInteractions(checkoutRepository);
    }

}
