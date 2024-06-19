package products.db;

import models.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductDB {

    Flux<Product> getProducts();

    Mono<Product> getProduct(Long productId);

    Flux<Product> searchProductByName(String name);
}
