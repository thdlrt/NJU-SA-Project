package models.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {
    private Product product;
    private int amount;
}
