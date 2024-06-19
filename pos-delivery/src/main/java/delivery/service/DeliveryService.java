package delivery.service;

import lombok.Getter;
import models.model.Order;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class DeliveryService {

    private final List<String> orders = new ArrayList<>();

    @RabbitListener(queues = "orderCreatedQueue")
    public void receiveOrder(String order) {
        orders.add(order);
    }

}
