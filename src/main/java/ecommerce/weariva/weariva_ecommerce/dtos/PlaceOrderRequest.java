package ecommerce.weariva.weariva_ecommerce.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlaceOrderRequest {

    private String street;
    private String phone;
    private String city;
    private String state;
    private String country;
    private String paymentmode;
}
