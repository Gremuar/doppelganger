package com.restapi.doppelganger.repository;

import java.util.List;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.restapi.doppelganger.entity.UserLink;

@Repository
public interface UserLinkRepository extends JpaRepository<UserLink, Long> {
  UserLink findFirstByLongLink(String url);
  UserLink findFirstByShortLink(String url);
  UserLink findTopByLongLink(String url);
  List<UserLink> findFirst100By();
  UserLink findFirstBy();
  @Query("SELECT max(dgId) FROM UserLink ")
  Long getMaxId();
}
