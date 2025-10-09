package ecommerce.weariva.weariva_ecommerce.models;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotNull
    @NotEmpty
    private String name;
    private String description;
    public String category;
    public String subcategory;
    private String imageUrl;
    private String publicId;
    private Integer stock;
    private List<String> sizes;
    private Integer discount;
    private Double price;
    private Double discountedPrice;
    private boolean isAvailable;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Rating> rating;

}
