package ch.admin.seco.jobroom.service.search;

import ch.admin.seco.jobroom.domain.Organization;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for the Organization entity.
 */
public interface OrganizationSearchRepository extends ElasticsearchRepository<Organization, UUID> {
}
