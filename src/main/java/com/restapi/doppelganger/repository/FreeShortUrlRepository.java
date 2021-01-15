package com.restapi.doppelganger.repository;

import com.restapi.doppelganger.entity.FreeShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeShortUrlRepository extends JpaRepository<FreeShortUrl, Long> {
  FreeShortUrl findFirstBy();
}
