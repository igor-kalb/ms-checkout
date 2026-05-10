package br.com.uri.mscheckout;

import br.com.uri.mscheckout.client.ProductClient;
import br.com.uri.mscheckout.client.domain.Product;
import br.com.uri.mscheckout.client.domain.ProductResult;
import br.com.uri.mscheckout.repository.CheckoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc
class CheckoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @MockBean
    private ProductClient productClient;

    @BeforeEach
    void setUp() {
        checkoutRepository.deleteAll();
    }

    private static final String VALID_REQUEST = """
            {
                "orderId": "order-001",
                "productRequest": {
                    "productId": "prod-1",
                    "salesPrice": 99.99,
                    "name": "Product A"
                }
            }
            """;

    @Test
    void checkout_shouldPersistEntity_whenProductIsInStock() throws Exception {
        when(productClient.validate(any())).thenReturn(inStockResponse("prod-1", 99.99));

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        var entities = checkoutRepository.findAll();
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getOrderId()).isEqualTo("order-001");
        assertThat(entities.get(0).getProductId()).isEqualTo("prod-1");
        assertThat(entities.get(0).getPrice().doubleValue()).isEqualTo(99.99);
    }

    @Test
    void checkout_shouldNotPersist_whenProductIsOutOfStock() throws Exception {
        when(productClient.validate(any())).thenReturn(outOfStockResponse());

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isUnprocessableEntity());

        assertThat(checkoutRepository.findAll()).isEmpty();
    }

    @Test
    void checkout_shouldNotPersist_whenExternalServiceFails() throws Exception {
        when(productClient.validate(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isUnprocessableEntity());

        assertThat(checkoutRepository.findAll()).isEmpty();
    }

    @Test
    void checkout_shouldReturn400_andNotPersist_whenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\": \"\"}"))
                .andExpect(status().isBadRequest());

        assertThat(checkoutRepository.findAll()).isEmpty();
    }

    @Test
    void checkout_shouldPersistMultipleOrders_whenCalledSequentially() throws Exception {
        when(productClient.validate(any())).thenReturn(inStockResponse("prod-1", 99.99));

        var secondRequest = VALID_REQUEST.replace("order-001", "order-002");

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequest))
                .andExpect(status().isOk());

        assertThat(checkoutRepository.findAll()).hasSize(2);
    }

    private ResponseEntity<ProductResult> inStockResponse(String productId, double price) {
        var product = new Product();
        product.setProductId(productId);
        product.setSalesPrice(price);
        product.setStatus("in stock");

        var result = new ProductResult();
        result.setBody(product);
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<ProductResult> outOfStockResponse() {
        var product = new Product();
        product.setStatus("out of stock");

        var result = new ProductResult();
        result.setBody(product);
        return ResponseEntity.ok(result);
    }

}
