package orders.web;

import models.model.Item;
import models.model.Order;
import orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/Order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/add/{productId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Mono<ResponseEntity<Boolean>> addProduct(@PathVariable Long productId) {
        return orderService.getProduct(productId)
                .flatMap(product -> orderService.addItem(productId).thenReturn(ResponseEntity.ok(true)))
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @GetMapping("/delete/{productId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Mono<ResponseEntity<Boolean>> deleteProduct(@PathVariable Long productId) {
        return orderService.getProduct(productId)
                .flatMap(product -> orderService.deleteItem(productId).thenReturn(ResponseEntity.ok(true)))
                .defaultIfEmpty(ResponseEntity.ok(false));
    }

    @GetMapping("/checkout")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Mono<ResponseEntity<Boolean>> checkCart() {
        return orderService.checkoutOrder().thenReturn(ResponseEntity.ok(true));
    }

    @GetMapping("/order/{orderId}")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Mono<ResponseEntity<Order>> getOrder(@PathVariable Integer orderId) {
        return orderService.getOrder(orderId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/cart")
    @CrossOrigin(value = "*", maxAge = 1800, allowedHeaders = "*")
    public Flux<Item> getCart() {
        return orderService.getCart();
    }
}
