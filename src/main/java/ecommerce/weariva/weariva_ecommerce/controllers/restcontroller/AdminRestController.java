package ecommerce.weariva.weariva_ecommerce.controllers.restcontroller;

import java.io.IOException;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ecommerce.weariva.weariva_ecommerce.dtos.ProductRequest;
import ecommerce.weariva.weariva_ecommerce.dtos.UpdateUser;
import ecommerce.weariva.weariva_ecommerce.models.Orders;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.models.User;
import ecommerce.weariva.weariva_ecommerce.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.services.OrderService;
import ecommerce.weariva.weariva_ecommerce.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.services.UserService;
import ecommerce.weariva.weariva_ecommerce.services.cloudinaryservice.CloudinaryServices;
import ecommerce.weariva.weariva_ecommerce.services.sseservices.SseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final CloudinaryServices cloudinaryService;
    private final SseService sseService;
    private final ProductService productService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final OrderService orderService;

    @PostMapping("addordertodelivery")
    public ResponseEntity<?> addDeliveryOrder(@RequestParam String username, @RequestParam Long orderId) {
        try {
            Orders order = this.orderService.getMyOrderById(orderId).orElse(null);
            if (!ObjectUtils.isEmpty(order)) {
                order.setDeliveryAgent(username);
                this.orderService.saveOrder(order);
                return ResponseEntity.ok().body(new RestApiResponse(true, "Successfully Alloted to " + username));
            } else {
                return ResponseEntity.ok().body(new RestApiResponse(false, "Order not Found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, "Internal Server Error"));
        }

    }

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

    @PostMapping(value = "/uploadproduct/{uniqueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@ModelAttribute ProductRequest productRequest, @RequestParam MultipartFile image,
            @PathVariable String uniqueId) {

        try {
            boolean isExists = this.productService.existsByProductName(productRequest.getName());
            if (!isExists) {
                byte[] data = image.getBytes();
                this.cloudinaryService.uploadProductImages(productRequest, data, uniqueId, "Weariva_Assets");
                return ResponseEntity.ok().body(new RestApiResponse(true, "Upload Started!!"));
            } else {
                throw new Exception("Already Exists with Same Name");
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping(value = "/updateproduct/{uniqueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(@ModelAttribute ProductRequest productRequest,
            @RequestParam(required = false) MultipartFile image, @PathVariable String uniqueId) throws IOException {
        try {
            Product previousProduct = productService.findProductByName(productRequest.getName());
            if (!ObjectUtils.isEmpty(previousProduct)) {
                if (image != null) {
                    byte[] data = image.getBytes();
                    this.cloudinaryService.uploadProductImages(productRequest, data, uniqueId, "Weariva_Assets");
                    return ResponseEntity.ok().body(new RestApiResponse(true, "Updating Started!!"));
                } else {
                    previousProduct.setName(productRequest.getName());
                    previousProduct.setCategory(productRequest.getCategory());
                    previousProduct.setSubcategory(productRequest.getSubcategory());
                    previousProduct.setDescription(productRequest.getDescription());
                    previousProduct.setDiscount(productRequest.getDiscount());
                    previousProduct.setStock(productRequest.getStock());
                    previousProduct.setSizes(productRequest.getSizes());
                    previousProduct.setPrice(productRequest.getPrice());
                    this.productService.saveProduct(previousProduct);
                    return ResponseEntity.ok().body(new RestApiResponse(true, "Successfully Updated!!"));
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new RestApiResponse(false, "Product Not Exists"));
        }
    }

    @PostMapping("/deleteproduct")
    public ResponseEntity<?> deleteproduct(@RequestParam Long id) {
        try {
            boolean deleteProductById = this.productService.deleteProductById(id);
            if (deleteProductById) {
                return ResponseEntity.ok().body(new RestApiResponse(true, "Successfully Removed"));
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new RestApiResponse(true, "Failed Removal."));
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
    public SseEmitter subscribe(@PathVariable String sessionId) {

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
