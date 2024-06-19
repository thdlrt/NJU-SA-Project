package models.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double price;

    private String category;

    private String img;

    private int stock;

    private int quantity;

    public Product(String name, double price, String img, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.img = img;
        this.stock = 1;
        this.quantity = 500;
    }
}
