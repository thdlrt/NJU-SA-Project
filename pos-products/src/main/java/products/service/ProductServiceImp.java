package products.service;

import models.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import products.db.ProductDB;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImp implements ProductService {

    private ProductDB productDB;

    @Autowired
    public void setProductDB(ProductDB productDB) {
        this.productDB = productDB;
    }

    @Override
    public Flux<Product> getAllProducts() {
        return productDB.getProducts();
    }

    @Override
    public Mono<Product> getProductById(Long productId) {
        return productDB.getProduct(productId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public Flux<Product> searchProductByName(String name) {
        return productDB.searchProductByName(name);
    }
}