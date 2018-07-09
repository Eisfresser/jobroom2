package ch.admin.seco.jobroom.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobroomApp.class)
public class UserInfoRepositoryTest {

    private static final String VALID_USER_EXT_ID_1 = "1111";
    private static final String VALID_USER_EXT_ID_2 = "2222";
    private static final String VALID_USER_EXT_ID_3 = "3333";
    private static final String INVALID_USER_EXT_ID = "9999";
    private static final String VALID_EXT_ID = "3456";

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void findById() {
        UserInfo userInfo = userInfoRepository.save(getDummyUser(VALID_USER_EXT_ID_1));

        Optional<UserInfo> userInfoRepositoryOne = userInfoRepository.findById(userInfo.getId());

        assertTrue(userInfoRepositoryOne.isPresent());
    }

    @Test
    public void findOneByUserExternalId() {
        userInfoRepository.save(getDummyUser(VALID_USER_EXT_ID_1));

        Optional<UserInfo> oneByUserExternalId = userInfoRepository.findOneByUserExternalId(VALID_USER_EXT_ID_1);
        assertTrue(oneByUserExternalId.isPresent());
        assertTrue(oneByUserExternalId.get().getLastName().equals("Muster"));
    }

    @Test
    public void findOneByUserExternalIdNotFound() {
        userInfoRepository.save(getDummyUser(VALID_USER_EXT_ID_2));

        Optional<UserInfo> oneByUserExternalId = userInfoRepository.findOneByUserExternalId(INVALID_USER_EXT_ID);
        assertTrue(!oneByUserExternalId.isPresent());
    }

    @Test
    public void saveUserWithCompany() {
        UserInfo user = userInfoRepository.save(getDummyUser(VALID_USER_EXT_ID_3));

        Company company = companyRepository.save(getDummyCompany());
        user.requestAccessAsEmployer(company);
        userInfoRepository.save(user);

        Optional<UserInfo> foundUser = userInfoRepository.findOneByUserExternalId(VALID_USER_EXT_ID_3);
        assertTrue(foundUser.isPresent());
        assertTrue(foundUser.get().getLastName().equals("Muster"));
        assertNotNull(foundUser.get().getCompany());
        assertTrue(foundUser.get().getCompany().getName().equals("ACME AG"));
        assertTrue(foundUser.get().getCompany().getExternalId().equals(VALID_EXT_ID));
    }

    private UserInfo getDummyUser(String extId) {
        return new UserInfo("Hans", "Muster", "hans.muster@example.com", extId, Constants.DEFAULT_LANGUAGE);
    }

    private Company getDummyCompany() {
        Company company = new Company("ACME AG", VALID_EXT_ID);
        company.setStreet("Sesamstreet");
        company.setZipCode("1234");
        company.setCity("New York");
        return company;
    }
}
