package ch.admin.seco.jobroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalTermsRepository extends JpaRepository<LegalTerms, String> {

    @Query("select lt from LegalTerms lt "
        + "where lt.effectiveAt < current_timestamp "
        + "order by lt.effectiveAt desc"
    )
    List<LegalTerms> findPastEffectiveLegalTerms();

    @Query("select lt from LegalTerms lt "
        + "order by lt.effectiveAt desc"
    )
    List<LegalTerms> findAllOrderedByEffectiveAtDesc();
}
