package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.domain.BlacklistedAgent;
import ch.admin.seco.jobroom.domain.BlacklistedAgentId;
import ch.admin.seco.jobroom.domain.BlacklistedAgentRepository;
import ch.admin.seco.jobroom.domain.BlacklistedAgentStatus;
import ch.admin.seco.jobroom.security.IsAdmin;
import ch.admin.seco.jobroom.service.dto.BlacklistedAgentDto;
import ch.admin.seco.jobroom.service.dto.OrganizationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.seco.jobroom.domain.BlacklistedAgent.builder;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Transactional
public class BlacklistedAgentService {

    private final Logger LOG = LoggerFactory.getLogger(BlacklistedAgentService.class);

    private final BlacklistedAgentRepository blacklistedAgentRepository;

    private final CurrentUserService currentUserService;

    private final OrganizationService organizationService;

    public BlacklistedAgentService(BlacklistedAgentRepository blacklistedAgentRepository,
                                   CurrentUserService currentUserService,
                                   OrganizationService organizationService) {
        this.blacklistedAgentRepository = blacklistedAgentRepository;
        this.currentUserService = currentUserService;
        this.organizationService = organizationService;
    }

    @IsAdmin
    public BlacklistedAgentId create(String externalId) throws OrganizationNotFoundException, BlacklistedAgentAlreadyExistsException {
        LOG.debug("Request to create a blacklisted agent for an organization externalId {}", externalId);
        OrganizationDTO organizationDTO = organizationService.findOneByExternalId(externalId)
            .orElseThrow(() -> new OrganizationNotFoundException(externalId));

        if (this.blacklistedAgentRepository.findByExternalId(organizationDTO.getExternalId()).isPresent()) {
            throw new BlacklistedAgentAlreadyExistsException(externalId);
        }
        BlacklistedAgent blacklistedAgent = this.blacklistedAgentRepository.save(createBlacklistedAgent(organizationDTO));
        return blacklistedAgent.getId();
    }

    @IsAdmin
    public void changeStatus(BlacklistedAgentId id, BlacklistedAgentStatus status) {
        LOG.debug("Request to deactivate a blacklisted agent with id {}", id);
        blacklistedAgentRepository.findById(id).ifPresent(blacklistedAgent -> blacklistedAgent.changeStatus(status, this.currentUserService.getPrincipal()));
    }

    @IsAdmin
    public List<BlacklistedAgentDto> findAll() {
        return this.blacklistedAgentRepository.findAll(Sort.by(DESC, "blacklistedAt")).stream()
            .map(this::toBlacklistedAgentDto)
            .collect(Collectors.toList());
    }

    private BlacklistedAgentDto toBlacklistedAgentDto(BlacklistedAgent blacklistedAgent) {
        return new BlacklistedAgentDto()
            .setId(blacklistedAgent.getId().getValue())
            .setExternalId(blacklistedAgent.getExternalId())
            .setName(blacklistedAgent.getName())
            .setStreet(blacklistedAgent.getStreet())
            .setZipCode(blacklistedAgent.getZipCode())
            .setCity(blacklistedAgent.getCity())
            .setCreatedBy(blacklistedAgent.getCreatedBy())
            .setBlacklistingCounter(blacklistedAgent.getBlacklistingCounter())
            .setStatus(blacklistedAgent.getStatus())
            .setBlacklistedAt(blacklistedAgent.getBlacklistedAt());
    }

    private BlacklistedAgent createBlacklistedAgent(OrganizationDTO organizationDTO) {
        return builder()
            .setId(new BlacklistedAgentId())
            .setExternalId(organizationDTO.getExternalId())
            .setName(organizationDTO.getName())
            .setStreet(organizationDTO.getStreet())
            .setZipCode(organizationDTO.getZipCode())
            .setCity(organizationDTO.getCity())
            .setCreatedBy(this.currentUserService.getPrincipal())
            .build();
    }

}
