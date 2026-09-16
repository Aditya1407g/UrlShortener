package com.aditya.urlshortener;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);



    List<Url> findByUserId(Long userId);

    /**
     * Increments the counter in the database rather than loading the entity,
     * adding one, and saving it back. That read-modify-write loses increments
     * when two redirects for the same code overlap; a single UPDATE cannot.
     */
    @Modifying
    @Transactional
    @Query("update Url u set u.clickCount = u.clickCount + 1 where u.shortCode = :shortCode")
    int incrementClickCount(@Param("shortCode") String shortCode);

}
