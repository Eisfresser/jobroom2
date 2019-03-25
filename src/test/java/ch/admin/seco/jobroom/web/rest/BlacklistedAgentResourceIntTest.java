package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.service.BlacklistedAgentService;
import ch.admin.seco.jobroom.service.CurrentUserService;
import ch.admin.seco.jobroom.service.OrganizationService;
import ch.admin.seco.jobroom.service.dto.OrganizationDTO;
import ch.admin.seco.jobroom.service.mapper.OrganizationMapper;
import ch.admin.seco.jobroom.web.rest.BlacklistedAgentResource.CreateNewBlacklistedAgentResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static ch.admin.seco.jobroom.domain.BlacklistedAgentStatus.INACTIVE;
import static ch.admin.seco.jobroom.domain.fixture.OrganizationFixture.testOrganization;
import static ch.admin.seco.jobroom.security.AuthoritiesConstants.ROLE_ADMIN;
import static ch.admin.seco.jobroom.web.rest.TestUtil.APPLICATION_JSON_UTF8;
import static ch.admin.seco.jobroom.web.rest.TestUtil.convertObjectToJsonBytes;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlacklistedAgentResourceIntTest {

    @MockBean
    private UserPrincipal userPrincipal;

    @MockBean
    private CurrentUserService currentUserService;

    @Autowired
    private BlacklistedAgentService blacklistedAgentService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper organizationMapper;

    private MockMvc restMock;

    @Before
    public void setup() {
        initMocks(this);
        BlacklistedAgentResource resource = new BlacklistedAgentResource(blacklistedAgentService);
        this.restMock = standaloneSetup(resource).build();
        given(currentUserService.getPrincipal()).willReturn(userPrincipal);
        given(userPrincipal.getUsername()).willReturn("username");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {ROLE_ADMIN})
    public void shouldAddOrganizationToBlacklist() throws Exception {
        OrganizationDTO organizationDTO = this.organizationService.save(organizationMapper.toDto(testOrganization()));
        restMock.perform(
            post("/api/blacklisted-agent/")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(new CreateNewBlacklistedAgentResource() {{
                    this.externalId = organizationDTO.getExternalId();
                }})))
            .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {ROLE_ADMIN})
    public void shouldChangeStatus() throws Exception {
        String id = this.organizationService.save(organizationMapper.toDto(testOrganization())).getExternalId();
        String blacklistedAgentId = blacklistedAgentService.create(id).getValue();

        restMock.perform(
            put("/api/blacklisted-agent/" + blacklistedAgentId + "/status")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJsonBytes(INACTIVE)))
            .andExpect(status().isOk());
    }
}
