package ch.admin.seco.jobroom.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static ch.admin.seco.jobroom.domain.BlacklistedAgentStatus.ACTIVE;
import static ch.admin.seco.jobroom.domain.fixture.BlacklistedAgentFixture.testBlacklistedAgent;
import static ch.admin.seco.jobroom.domain.fixture.UserPrincipalFixture.testPrincipal;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
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
