package br.com.uri.mscheckout.controller;

import br.com.uri.mscheckout.exception.ProductNotValidException;
import br.com.uri.mscheckout.service.Checkout;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("null")
@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Checkout checkout;

    private static final String VALID_REQUEST = """
            {
                "orderId": "order-1",
                "productRequest": {
                    "productId": "prod-1",
                    "salesPrice": 10.0,
                    "name": "Product A"
                }
            }
            """;

    @Test
    void checkout_shouldReturn200_whenRequestIsValid() throws Exception {
        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(checkout).doCheckout(any());
    }

    @Test
    void checkout_shouldReturn400_whenOrderIdIsBlank() throws Exception {
        var body = VALID_REQUEST.replace("\"order-1\"", "\"\"");

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkout_shouldReturn400_whenProductRequestIsAbsent() throws Exception {
        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\": \"order-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkout_shouldReturn400_whenProductIdIsBlank() throws Exception {
        var body = VALID_REQUEST.replace("\"prod-1\"", "\"\"");

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkout_shouldReturn400_whenSalesPriceIsNegative() throws Exception {
        var body = VALID_REQUEST.replace("10.0", "-1.0");

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkout_shouldReturn422_whenProductServiceThrows() throws Exception {
        doThrow(new ProductNotValidException("Product out of stock"))
                .when(checkout).doCheckout(any());

        mockMvc.perform(post("/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void ping_shouldReturn500WithPong() throws Exception {
        mockMvc.perform(get("/v1/ping"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("pong"));
    }

}
