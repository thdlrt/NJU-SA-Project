package products.db;

import products.client.ProductClient;
import models.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductDBImp implements ProductDB {

    @Autowired
    private ProductClient productClient;

    @Override
    public Flux<Product> getProducts() {
        return Flux.fromIterable(productClient.getProducts());
    }

    @Override
    public Mono<Product> getProduct(Long productId) {
        return Mono.fromCallable(() -> productClient.getProductById(productId));
    }

    @Override
    public Flux<Product> searchProductByName(String name) {
        return Flux.fromIterable(productClient.searchProductByName(name));
    }
}
