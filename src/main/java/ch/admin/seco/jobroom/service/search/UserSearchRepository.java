package ch.admin.seco.jobroom.service.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for the User entity.
 */
public interface UserSearchRepository extends ElasticsearchRepository<UserDocument, UUID> {
}
