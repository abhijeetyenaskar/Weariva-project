package ecommerce.weariva.weariva_ecommerce.services.cloudinaryservice;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import ecommerce.weariva.weariva_ecommerce.dtos.ProductRequest;
import ecommerce.weariva.weariva_ecommerce.models.Product;
import ecommerce.weariva.weariva_ecommerce.services.ProductService;
import ecommerce.weariva.weariva_ecommerce.services.sseservices.SseService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloudinaryServices {

    private final Cloudinary cloudinary;
    private final SseService sseService;
    private final ProductService productService;

    @Async
    public void uploadProductImages(ProductRequest productRequest, byte[] image,
            String sessionId,
            String folder) {
        File tempFile = null;
        try {

            tempFile = File.createTempFile("upload-", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(image);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadParams = ObjectUtils.asMap("folder", folder, "public_id",
                    UUID.randomUUID().toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(tempFile, uploadParams);
            String secure_url = (String) result.get("secure_url");
            this.sseService.sendEmitterEvent(sessionId, "image", "Product Image Uploaded!");
            if (!org.springframework.util.ObjectUtils.isEmpty(secure_url)) {
                Product product = Product.builder().name(productRequest.getName())
                        .category(productRequest.getCategory()).subcategory(productRequest.getSubcategory())
                        .description(productRequest.getDescription()).discount(productRequest.getDiscount())
                        .publicId((String) result.get("secure_url"))
                        .sizes(productRequest.getSizes()).stock(productRequest.getStock()).imageUrl(secure_url)
                        .price(Math.ceil(productRequest.getPrice()))
                        .discountedPrice(Math.ceil(productRequest.getPrice()
                                - ((productRequest.getPrice() * productRequest.getDiscount()) / 100)))
                        .isAvailable(true).build();
                this.productService.saveProduct(product);
                this.sseService.sendEmitterEvent(sessionId, "data", "Product Successfully Uploaded!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sseService.sendEmitterEvent(sessionId, "product", "Product Uploading Failed!");
        }
    }

}