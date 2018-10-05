package ch.admin.seco.jobroom.domain.fixture;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.User;

public class UserFixture {

    public static User testUser() {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail("john.doe@example.com");
        return user;
    }
}
