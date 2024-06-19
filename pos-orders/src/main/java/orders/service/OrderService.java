package orders.service;

import models.model.Item;
import models.model.Order;
import models.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {
    Mono<Void> checkoutOrder();

    Mono<Void> addItem(Long productId);

    Mono<Void> deleteItem(Long productId);

    Mono<Order> getOrder(int orderId);

    Mono<Product> getProduct(Long productId);

    Flux<Item> getCart();
}
