package ecommerce.weariva.weariva_ecommerce.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductRequest {

    private String name;
    private String description;
    private String category;
    private String subcategory;
    private List<String> sizes;
    private Integer stock;
    private Double price;
    private Integer discount;
    
}
