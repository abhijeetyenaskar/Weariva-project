package ecommerce.weariva.weariva_ecommerce.product.restcontrollers;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ecommerce.weariva.weariva_ecommerce.common.cloudinaryservice.CloudinaryServices;
import ecommerce.weariva.weariva_ecommerce.common.records.RestApiResponse;
import ecommerce.weariva.weariva_ecommerce.product.dtos.ProductRequest;
import ecommerce.weariva.weariva_ecommerce.product.models.Product;
import ecommerce.weariva.weariva_ecommerce.product.services.ProductService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductRestController {

    private final CloudinaryServices cloudinaryService;
    private final ProductService productService;

    @PostMapping(value = "/admin/uploadproduct/{uniqueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping(value = "/admin/updateproduct/{uniqueId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @PostMapping("/admin/deleteproduct")
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

}
