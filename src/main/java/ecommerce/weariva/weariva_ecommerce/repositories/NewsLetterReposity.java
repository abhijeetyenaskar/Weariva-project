package ecommerce.weariva.weariva_ecommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.weariva.weariva_ecommerce.models.NewsLetterEmail;

@Repository
public interface NewsLetterReposity extends JpaRepository<NewsLetterEmail, Long> {

    public boolean existsByEmail(String email);
}