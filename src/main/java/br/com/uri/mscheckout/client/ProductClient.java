package br.com.uri.mscheckout.client;

import br.com.uri.mscheckout.client.domain.Product;
import br.com.uri.mscheckout.client.domain.ProductResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "product", url = "${clients.product.url}")
public interface ProductClient {

    @PostMapping("/v1/products/validate")
    ResponseEntity<ProductResult> validate(@RequestBody Product product);

}
