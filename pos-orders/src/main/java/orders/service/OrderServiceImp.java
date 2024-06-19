package orders.service;

import models.model.Item;
import models.model.Order;
import models.model.Product;
import orders.db.OrderDB;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceImp implements OrderService {
    private OrderDB orderDB;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderServiceImp(OrderDB orderDB, RabbitTemplate rabbitTemplate) {
        this.orderDB = orderDB;
        this.rabbitTemplate = rabbitTemplate;
    }

    void sendOrder(Order order){
        StringBuilder msg = new StringBuilder();
        msg.append("Order:");
        msg.append(System.currentTimeMillis()).append(":");
        msg.append(order.getOrderId()).append(":");
        for(Item item : order.getItems()){
            msg.append(item.getProduct().getId()).append(":").append(item.getAmount()).append(",");
        }
        rabbitTemplate.convertAndSend("orderCreatedQueue", msg.toString());
    }

    @Override
    public Mono<Void> checkoutOrder() {
        return orderDB.saveOrder()
                .doOnNext(this::sendOrder)
                .then();
    }

    @Override
    public Mono<Void> addItem(Long productId) {
        return orderDB.changeItem(productId, 1);
    }

    @Override
    public Mono<Void> deleteItem(Long productId) {
        return orderDB.getItem(productId)
                .flatMap(item -> orderDB.changeItem(productId, -1 * item.getAmount()));
    }

    @Override
    public Mono<Order> getOrder(int orderId) {
        return orderDB.getOrder(orderId);
    }

    @Override
    public Mono<Product> getProduct(Long productId) {
        return orderDB.getProduct(productId);
    }

    @Override
    public Flux<Item> getCart() {
        return orderDB.getCart();
    }
}
