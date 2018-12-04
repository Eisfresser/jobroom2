package ch.admin.seco.jobroom.repository;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.domain.LegalTerms;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ch.admin.seco.jobroom.domain.fixture.LegalTermsFixture.testLegalTermsFixture;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional()
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobroomApp.class)
public class LegalTermsRepositoryTest {

    private List<LegalTerms> testLegalTerms;

    @Autowired
    private LegalTermsRepository legalTermsRepository;

    @Before
    public void given() {
        testLegalTerms = asList(
            testLegalTermsFixture()
                .setEffectiveAt(LocalDate.of(2019, 9, 1)),
            testLegalTermsFixture()
                .setEffectiveAt(LocalDate.of(2018, 9, 1)),
            testLegalTermsFixture()
                .setEffectiveAt(LocalDate.of(2018, 10, 2 ))
        );
        legalTermsRepository.saveAll(
            testLegalTerms
        );
    }

    @Test
    public void shouldFindPastEffectiveLegalTerms() {
        //when
        List<LegalTerms> legalTerms = legalTermsRepository.findPastEffectiveLegalTerms();

        //then
        assertThat(legalTerms)
            .containsExactly(testLegalTerms.get(2), testLegalTerms.get(1));
    }

    @Test
    public void shouldFindDistinctByEffectiveAt() {
        //when
        List<LegalTerms> legalTerms = legalTermsRepository.findAllOrderedByEffectiveAtDesc();

        //then
        assertThat(legalTerms)
            .containsExactly(testLegalTerms.get(0), testLegalTerms.get(2), testLegalTerms.get(1));
    }
}
