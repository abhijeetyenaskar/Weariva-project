package ecommerce.weariva.weariva_ecommerce.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class OrderAddress {

    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;

}
