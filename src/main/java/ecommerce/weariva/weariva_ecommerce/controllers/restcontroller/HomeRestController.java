package ecommerce.weariva.weariva_ecommerce.controllers.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecommerce.weariva.weariva_ecommerce.models.NewsLetterEmail;
import ecommerce.weariva.weariva_ecommerce.repositories.NewsLetterReposity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/home")
@RequiredArgsConstructor
public class HomeRestController {

    private final NewsLetterReposity newsRepository;

    @PostMapping("saveemail")
    public ResponseEntity<?> saveNewsEmail(@RequestParam String email) {
        try {
            boolean existsByEmail = this.newsRepository.existsByEmail(email);
            if (existsByEmail) {
                return ResponseEntity.ok().body(new NewsletterResponse(true, "You have already Joined Us."));
            }
            
            this.newsRepository.save(NewsLetterEmail.builder().email(email).build());
            return ResponseEntity.ok().body(new NewsletterResponse(true, "Successfully Added."));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new NewsletterResponse(false, "Internal Server Error."));
        }

    }

    private record NewsletterResponse(Boolean flag, String message) {
    }
}
