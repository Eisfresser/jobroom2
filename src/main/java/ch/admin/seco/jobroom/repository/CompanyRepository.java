package ch.admin.seco.jobroom.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.CompanyId;


/**
 * Spring Data JPA repository for the Company entity.
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, CompanyId> {

    Optional<Company> findByExternalId(String externalId);

    @Query("select c from Company c")
    Stream<Company> streamAll();
}
