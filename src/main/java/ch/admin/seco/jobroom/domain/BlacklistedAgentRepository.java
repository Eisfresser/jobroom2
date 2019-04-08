package ch.admin.seco.jobroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlacklistedAgentRepository extends JpaRepository<BlacklistedAgent, BlacklistedAgentId> {

    @Query("select ba from BlacklistedAgent ba where ba.externalId = :externalId")
    Optional<BlacklistedAgent> findByExternalId(@Param("externalId") String externalId);

    @Query("select ba from BlacklistedAgent ba where ba.externalId = :externalId and ba.status = 'ACTIVE'")
    Optional<BlacklistedAgent> findActiveByExternalId(@Param("externalId") String externalId);
}
