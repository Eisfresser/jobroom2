package ch.admin.seco.jobroom.web.rest;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.security.IsSystemAdmin;
import ch.admin.seco.jobroom.security.SecurityUtils;
import ch.admin.seco.jobroom.service.ElasticsearchIndexService;
import ch.admin.seco.jobroom.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Elasticsearch index.
 */
@RestController
@RequestMapping("/api")
public class ElasticsearchIndexResource {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexResource.class);

    private final ElasticsearchIndexService elasticsearchIndexService;

    public ElasticsearchIndexResource(ElasticsearchIndexService elasticsearchIndexService) {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    /*
     * POST  /elasticsearch/index -> Reindex all Elasticsearch documents
     */
    @PostMapping("/elasticsearch/index")
    @Timed
    @IsSystemAdmin
    public ResponseEntity<Void> reindexAll() {
        log.info("REST request to reindex Elasticsearch by user : {}", SecurityUtils.getCurrentUserLogin());
        elasticsearchIndexService.reindexAll();
        return ResponseEntity.accepted()
            .headers(HeaderUtil.createAlert("elasticsearch.reindex.jobroom.accepted", ""))
            .build();
    }
}
