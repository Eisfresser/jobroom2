package ch.admin.seco.jobroom.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.admin.seco.jobroom.domain.UserInfo;

/**
 * Spring Data JPA repository for the UserInfo entity.
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

    String USER_INFOS_BY_EXTID_CACHE = "userInfosByUserExternalId";

    @Cacheable(cacheNames = USER_INFOS_BY_EXTID_CACHE)
    Optional<UserInfo> findOneByUserExternalId(String userExternalId);

    @Query("select u from UserInfo u")
    Stream<UserInfo> streamAll();
}
