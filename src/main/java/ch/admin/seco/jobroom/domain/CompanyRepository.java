package ch.admin.seco.jobroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * Spring Data JPA repository for the Company entity.
 */
public interface CompanyRepository extends JpaRepository<Company, CompanyId> {

    Optional<Company> findByExternalId(String externalId);

    @Query("select c from Company c")
    Stream<Company> streamAll();
}
