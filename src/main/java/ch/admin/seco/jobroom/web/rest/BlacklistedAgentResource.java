package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.domain.BlacklistedAgentId;
import ch.admin.seco.jobroom.domain.BlacklistedAgentStatus;
import ch.admin.seco.jobroom.service.BlacklistedAgentAlreadyExistsException;
import ch.admin.seco.jobroom.service.BlacklistedAgentService;
import ch.admin.seco.jobroom.service.OrganizationNotFoundException;
import ch.admin.seco.jobroom.service.dto.BlacklistedAgentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing blacklisted agents.
 */
@RestController
@RequestMapping("/api/blacklisted-agent")
public class BlacklistedAgentResource {

    private final Logger LOG = LoggerFactory.getLogger(BlacklistedAgentResource.class);

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
        this.blacklistedAgentService.create(createNewBlacklistedAgentResource.organizationId);
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
