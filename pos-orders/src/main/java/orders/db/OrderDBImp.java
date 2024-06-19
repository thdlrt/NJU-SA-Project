package orders.db;

import models.model.Product;
import models.model.Order;
import models.model.Item;
import orders.client.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class OrderDBImp implements OrderDB {
    private final List<Order> orders = new ArrayList<>();
    private final List<Item> cart = new ArrayList<>();
    int orderId = 0;
    private final ProductClient productClient;

    @Autowired
    public OrderDBImp(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Override
    public Mono<Product> getProduct(Long productId) {
        return Mono.fromCallable(() -> productClient.getProductById(productId));
    }

    @Override
    public Mono<Order> saveOrder() {
        orderId++;
        Order newOrder = new Order(orderId, List.copyOf(cart));
        orders.add(newOrder);
        cart.clear();
        return Mono.just(newOrder);
    }

    @Override
    public Mono<Order> getOrder(int orderId) {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return Mono.just(order);
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> changeItem(Long productId, int deltaAmount) {
        return Mono.fromRunnable(() -> {
            for (Item item : cart) {
                if (Objects.equals(item.getProduct().getId(), productId)) {
                    item.setAmount(item.getAmount() + deltaAmount);
                    if (item.getAmount() == 0) {
                        cart.remove(item);
                    }
                    return;
                }
            }
            if (deltaAmount > 0) {
                cart.add(new Item(getProduct(productId).block(), deltaAmount));
            }
        });
    }

    @Override
    public Mono<Item> getItem(Long productId) {
        for (Item item : cart) {
            if (Objects.equals(item.getProduct().getId(), productId)) {
                return Mono.just(item);
            }
        }
        return Mono.empty();
    }

    @Override
    public Flux<Item> getCart() {
        return Flux.fromIterable(cart);
    }
}
