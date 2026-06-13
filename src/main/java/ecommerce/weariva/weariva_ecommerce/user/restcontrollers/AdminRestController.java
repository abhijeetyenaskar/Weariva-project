package ecommerce.weariva.weariva_ecommerce.user.restcontrollers;

import java.io.IOException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ecommerce.weariva.weariva_ecommerce.common.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.common.sseservices.SseService;
import ecommerce.weariva.weariva_ecommerce.user.dtos.UpdateUser;
import ecommerce.weariva.weariva_ecommerce.user.models.User;
import ecommerce.weariva.weariva_ecommerce.user.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final SseService sseService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("delete-delivery-boy")
    public ResponseEntity<?> deleteDeliveryBoy(@RequestParam Long deliverBoyId) {
        try {
            User user = this.userService.getUserById(deliverBoyId).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                this.userService.deleteUser(deliverBoyId);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Delivery Boy Deleted"));
            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "Delivery-Boy Not Found"));
            }
        } catch (Exception e) {

            return ResponseEntity.ok().body(new RestApiResponse(false, "Internal Server Error"));
        }

    }

    @PostMapping("status-delivery-boy")
    public ResponseEntity<?> statusDeliveryBoy(@RequestParam Long deliverBoyId) {
        try {
            User user = this.userService.getUserById(deliverBoyId).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                user.setUserActive(!user.isUserActive());
                this.userService.saveUser(user);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Status Changed"));
            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "Delivery-Boy Not Found"));
            }
        } catch (Exception e) {

            return ResponseEntity.ok().body(new RestApiResponse(false, "Internal Server Error"));
        }

    }

    @PostMapping("/uploadsadmin")
    public ResponseEntity<?> uploadUser(@RequestBody User user) throws IOException {
        try {
            if (this.userService.existByCurrentUsername(user.getUsername())) {
                throw new Exception();
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRoles("ADMIN");
                user.setAccountNonLocked(true);
                user.setFailedCount(0);
                user.setUserActive(true);
                user.setLoggedIn(false);
                user.setUserLockedTime(null);
                User savedUser = this.userService.saveUser(user);
                return ResponseEntity.ok()
                        .body(new RestApiResponse(true, savedUser.getUserId() + ""));
            }
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(new RestApiResponse(false, "Admin with Same Name Already Exists"));
        }
    }

    @PostMapping("/change-admin-active")
    public ResponseEntity<?> changeAdminStatus(@RequestParam Long id) {
        try {
            User user = this.userService.getUserById(id).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                user.setUserActive(!user.isUserActive());
                this.userService.saveUser(user);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Active Status Changed."));
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            return ResponseEntity.ok().body(new RestApiResponse(true, "Active Status Failed."));
        }
    }

    @PostMapping("/delete-admin")
    public ResponseEntity<?> deleteAdminCurrent(@RequestParam Long id) {
        try {
            User user = this.userService.getUserById(id).orElse(null);
            if (!ObjectUtils.isEmpty(user)) {
                this.userService.deleteUser(id);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Admin Deleted."));
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            return ResponseEntity.ok().body(new RestApiResponse(true, "Deletion Failed."));
        }
    }

    @PostMapping("/adddeliveryboy")
    public ResponseEntity<?> addDeliveryBoy(@RequestBody User user) throws IOException {
        try {
            if (this.userService.existByCurrentUsername(user.getUsername())) {
                return ResponseEntity.ok()
                        .body(new RestApiResponse(false, "Already Exists with Same Username."));
            } else {
                user.setUsername(user.getUsername());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRoles("DELIVERY");
                user.setAccountNonLocked(true);
                user.setFailedCount(0);
                user.setUserActive(true);
                user.setLoggedIn(false);
                user.setUserLockedTime(null);
                User saveUser = this.userService.saveUser(user);
                if (saveUser != null) {
                    return ResponseEntity.ok()
                            .body(new RestApiResponse(true, saveUser.getUserId() + ""));
                } else {
                    return ResponseEntity.ok()
                            .body(new RestApiResponse(false, "Failed Delivery Add."));
                }
            }
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(new RestApiResponse(false, "Internal Server Error"));
        }
    }

    @PostMapping("updateprofile")
    public ResponseEntity<?> updateProfile(@ModelAttribute UpdateUser updateUser) {

        try {
            User user = this.userService
                    .getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElse(null);
            if (!org.springframework.util.ObjectUtils.isEmpty(user)) {
                user.setName(updateUser.getName());
                user.setUsername(updateUser.getUsername());
                user.setEmail(updateUser.getEmail());
                user.setPhone(updateUser.getPhone());
                user.setGender(updateUser.getGender());
                user.setCity(updateUser.getCity());
                user.setState(updateUser.getState());
                user.setPin(updateUser.getPin());
                this.userService.saveUser(user);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Profile Successfully Updated!!"));
            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "No User Found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Internal Server Error"));
        }
    }

    @GetMapping(value = "sse-uploadimage/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable @NonNull String sessionId) {

        SseEmitter emitter = this.sseService.createEmitter(sessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(sessionId).name("init").data("Stream Initiated.").build());
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
        return emitter;
    }

    @GetMapping(value = "/dashboard-stream-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeOrderNotifications() {
        SseEmitter emitter = this.sseService.createNotificationEmitter();
        return emitter;
    }

}
