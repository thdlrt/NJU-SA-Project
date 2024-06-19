package products.web;

import models.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import products.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 获取所有产品列表
    @GetMapping
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Flux<Product> listProducts() {
        return productService.getAllProducts();
    }

    // 根据ID获取特定产品详细信息
    @GetMapping("/{productId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Mono<ResponseEntity<Product>> showProductById(@PathVariable("productId") Long productId) {
        return productService.getProductById(productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/{name}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Flux<Product> searchProductByName(@PathVariable("name") String name) {
        return productService.searchProductByName(name);
    }
}
