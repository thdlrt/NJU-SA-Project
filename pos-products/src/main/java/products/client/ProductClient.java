package products.client;

import models.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "models-service")
public interface ProductClient {

    @GetMapping("/products")
    List<Product> getProducts();

    @GetMapping("/products/{id}")
    Product getProductById(@PathVariable("id") Long id);

    @PutMapping("/products/{id}")
    Product updateProduct(@PathVariable("id") Long id, @RequestBody Product product);

    @GetMapping("/products/search/{name}")
    List<Product> searchProductByName(@PathVariable("name") String name);
}
