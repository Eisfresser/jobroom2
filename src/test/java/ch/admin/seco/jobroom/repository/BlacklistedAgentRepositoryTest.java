package ch.admin.seco.jobroom.repository;

import static ch.admin.seco.jobroom.domain.BlacklistedAgentStatus.ACTIVE;
import static ch.admin.seco.jobroom.domain.fixture.BlacklistedAgentFixture.testBlacklistedAgent;
import static ch.admin.seco.jobroom.domain.fixture.UserPrincipalFixture.testPrincipal;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import ch.admin.seco.jobroom.domain.BlacklistedAgentStatus;
import ch.admin.seco.jobroom.domain.fixture.UserPrincipalFixture;
import ch.admin.seco.jobroom.security.UserPrincipal;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.domain.BlacklistedAgent;

@Transactional()
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobroomApp.class)
public class BlacklistedAgentRepositoryTest {

    @Autowired
    private BlacklistedAgentRepository blacklistedAgentRepository;

    @Test
    public void shouldFindByExternalId() {
        //given
        BlacklistedAgent savedBlacklistedAgent = blacklistedAgentRepository.save(
            testBlacklistedAgent().build()
        );

        //when
        Optional<BlacklistedAgent> blacklistedAgent = blacklistedAgentRepository.findByExternalId(savedBlacklistedAgent.getExternalId());

        //then
        assertThat(blacklistedAgent.isPresent()).isTrue();
        assertThat(savedBlacklistedAgent).isEqualTo(blacklistedAgent.get());
    }

    @Test
    public void shouldFindActiveByExternalId() {
        //given
        BlacklistedAgent blacklistedAgent = testBlacklistedAgent().build();
        blacklistedAgent.changeStatus(ACTIVE, testPrincipal());
        BlacklistedAgent savedBlacklistedAgent = blacklistedAgentRepository.save(blacklistedAgent);

        //when
        Optional<BlacklistedAgent> activeBlacklistedAgent = blacklistedAgentRepository.findActiveByExternalId(savedBlacklistedAgent.getExternalId());

        //then
        assertThat(activeBlacklistedAgent.isPresent()).isTrue();
        assertThat(savedBlacklistedAgent).isEqualTo(activeBlacklistedAgent.get());
    }
}
