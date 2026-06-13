package ecommerce.weariva.weariva_ecommerce.newsletter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.newsletter.models.NewsLetterEmail;

@Repository
public interface NewsLetterReposity extends JpaRepository<NewsLetterEmail, Long> {

    public boolean existsByEmail(String email);
}