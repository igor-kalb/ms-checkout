package br.com.uri.mscheckout.service;

import br.com.uri.mscheckout.client.domain.ProductResult;
import br.com.uri.mscheckout.controller.request.ProductRequest;

public interface ProductService {

    ProductResult get(ProductRequest productRequest);

}
