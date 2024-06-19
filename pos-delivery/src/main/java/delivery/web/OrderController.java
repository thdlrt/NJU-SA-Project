package delivery.web;

import models.model.Order;
import delivery.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Delivery")
public class OrderController {

    private final DeliveryService orderReceiverService;

    @Autowired
    public OrderController(DeliveryService orderReceiverService) {
        this.orderReceiverService = orderReceiverService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllOrders() {
        List<String> orders = orderReceiverService.getOrders();
        return ResponseEntity.ok(orders);
    }
}
