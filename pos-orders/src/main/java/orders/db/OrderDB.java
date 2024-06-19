package orders.db;

import models.model.Item;
import models.model.Order;
import models.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderDB {

    Mono<Product> getProduct(Long productId);

    Mono<Order> saveOrder();

    Mono<Order> getOrder(int orderId);

    Mono<Void> changeItem(Long productId, int amount);

    Mono<Item> getItem(Long productId);

    Flux<Item> getCart();
}
