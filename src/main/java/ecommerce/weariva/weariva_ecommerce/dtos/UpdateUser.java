package ecommerce.weariva.weariva_ecommerce.dtos;

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
public class UpdateUser {

    private String name;
    private String username;
    private String email;
    private String phone;
    private String gender;
    private String city;
    private String state;
    private String pin;

}
