package products.service;

import models.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> getAllProducts();
    Mono<Product> getProductById(Long productId);
    Flux<Product> searchProductByName(String name);
}