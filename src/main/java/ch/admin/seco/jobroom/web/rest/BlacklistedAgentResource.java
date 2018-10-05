package ch.admin.seco.jobroom.web.rest;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.domain.BlacklistedAgentId;
import ch.admin.seco.jobroom.domain.BlacklistedAgentStatus;
import ch.admin.seco.jobroom.service.BlacklistedAgentAlreadyExistsException;
import ch.admin.seco.jobroom.service.BlacklistedAgentService;
import ch.admin.seco.jobroom.service.OrganizationNotFoundException;
import ch.admin.seco.jobroom.service.dto.BlacklistedAgentDto;

/**
 * REST controller for managing blacklisted agents.
 */
@RestController
@RequestMapping("/api/blacklisted-agent")
public class BlacklistedAgentResource {

    private final Logger LOG = LoggerFactory.getLogger(OrganizationResource.class);

    private final BlacklistedAgentService blacklistedAgentService;

    public BlacklistedAgentResource(BlacklistedAgentService blacklistedAgentService) {
        this.blacklistedAgentService = blacklistedAgentService;
    }

    @GetMapping("/")
    public List<BlacklistedAgentDto> findAll() {
        LOG.debug("REST request to find all blacklisted agents");
        return this.blacklistedAgentService.findAll();
    }

    @PostMapping("/")
    public void create(@RequestBody CreateNewBlacklistedAgentResource createNewBlacklistedAgentResource)
        throws OrganizationNotFoundException, BlacklistedAgentAlreadyExistsException {
        LOG.debug("REST request to create a new blacklisted agent with organization id: {}", createNewBlacklistedAgentResource.organizationId);
        this.blacklistedAgentService.create(UUID.fromString(createNewBlacklistedAgentResource.organizationId));
    }

    @PutMapping("/{id}/status")
    public void changeStatus(@PathVariable String id, @RequestBody BlacklistedAgentStatus status) {
        LOG.debug("REST request to change a status of a blacklisted agent with id {} to {}", id, status.name());
        this.blacklistedAgentService.changeStatus(new BlacklistedAgentId(id), status);
    }

    static class CreateNewBlacklistedAgentResource {
        public String organizationId;
    }
}
