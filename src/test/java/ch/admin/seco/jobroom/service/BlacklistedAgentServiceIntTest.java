package ch.admin.seco.jobroom.service;

import static ch.admin.seco.jobroom.domain.fixture.OrganizationFixture.testOrganization;
import static ch.admin.seco.jobroom.security.AuthoritiesConstants.ROLE_ADMIN;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobroom.repository.BlacklistedAgentRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.service.mapper.OrganizationMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlacklistedAgentServiceIntTest {

    @MockBean
    private UserPrincipal userPrincipal;

    @MockBean
    private CurrentUserService currentUserService;

    @Autowired
    private BlacklistedAgentRepository blacklistedAgentRepository;

    @Autowired
    private BlacklistedAgentService service;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OrganizationMapper mapper;

    @Before
    public void setUp() {
        initMocks(this);
        given(currentUserService.getPrincipal()).willReturn(userPrincipal);
        given(userPrincipal.getUsername()).willReturn("username");
    }

    @Test
    @WithMockUser(authorities = {ROLE_ADMIN})
    public void shouldCreate() throws OrganizationNotFoundException, BlacklistedAgentAlreadyExistsException {
        UUID organizationId = organizationService.save(mapper.toDto(testOrganization())).getId();

        service.create(organizationId);

        assertThat(service.findAll()).size().isEqualTo(1);
    }

    @Test
    @WithMockUser(authorities = {ROLE_ADMIN})
    public void shouldNotCreateIfBlacklistedAgentAlreadyExists() {
        UUID organizationId = organizationService.save(mapper.toDto(testOrganization())).getId();

        assertThatThrownBy(() -> {
            service.create(organizationId);
            service.create(organizationId);
        }).isInstanceOf(BlacklistedAgentAlreadyExistsException.class)
            .hasMessageContaining("There is already an existing blacklisted agent for organization:");
    }

    @Test
    @WithMockUser(authorities = {ROLE_ADMIN})
    public void shouldNotCreateIfOrganizationNotFound() {
        given(currentUserService.getPrincipal()).willReturn(userPrincipal);

        assertThatThrownBy(() -> service.create(randomUUID()))
            .isInstanceOf(OrganizationNotFoundException.class)
            .hasMessageContaining("No Organization found having Id:");
    }

    @Test
    @WithMockUser
    public void shouldNotCreateIfNoAdminRole()  {
        assertThatThrownBy(() -> service.create(randomUUID())).isInstanceOf(AccessDeniedException.class);
    }
}
