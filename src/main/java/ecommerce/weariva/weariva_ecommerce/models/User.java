package ecommerce.weariva.weariva_ecommerce.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String name;

    private String username;
    private String email;

    private String password;

    private String phone;
    private String gender;
    private String city;
    private String state;
    private String pin;
    private String roles;

    private boolean userActive;

    private boolean isAccountNonLocked;
    private int FailedCount;
    private LocalDateTime userLockedTime;
    private boolean loggedIn;
    private String passwordResetToken;
    private LocalDateTime createdAt;

}
