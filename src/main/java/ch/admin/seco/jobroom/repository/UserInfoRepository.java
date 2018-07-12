package ch.admin.seco.jobroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.UserInfoId;

/**
 * Spring Data JPA repository for the UserInfo entity.
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UserInfoId> {

    @Query("select u from UserInfo u where u.userExternalId = :userExternalId")
    Optional<UserInfo> findOneByUserExternalId(@Param("userExternalId") String userExternalId);

    @EntityGraph(attributePaths = "accountabilities")
    @Query("select u from UserInfo u where u.id = :id")
    Optional<UserInfo> findOneWithAccountabilites(@Param("id") UserInfoId id);

    @Query("select u from UserInfo u where u.email = :eMail")
    Optional<UserInfo> findByEMail(@Param("eMail") String eMail);

    @Query("select u from UserInfo u where u.stesInformation.personNumber = :personNumber")
    Optional<UserInfo> findByPersonNumber(@Param("personNumber") Long personNumber);
}
