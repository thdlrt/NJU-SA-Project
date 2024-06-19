package models.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Order {
    int orderId;
    List<Item> items;

}
