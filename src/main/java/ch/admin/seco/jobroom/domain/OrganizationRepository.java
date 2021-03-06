package ch.admin.seco.jobroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


/**
 * Spring Data JPA repository for the Organization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByExternalId(String externalId);

    Stream<Organization> findByLastModifiedDateIsBefore(Instant instant);

    @Query("select o from Organization o")
    Stream<Organization> streamAll();
}
