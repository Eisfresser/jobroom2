package ch.admin.seco.jobroom.security.saml;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

public class EiamRoleMapperTest {

    @Test
    public void name() {
        // given
        ImmutableMap<String, String> mappings = ImmutableMap.<String, String>builder()
            .put("ROLE_ALLOW", "ALV-jobroom.ALLOW")
            .put("ROLE_REGISTRATION", "ALV-jobroom.ROLE_REGISTRATION")
            .put("ROLE_USER", "ALV-jobroom.ROLE_USER")
            .put("ROLE_JOBSEEKER_CLIENT", "ALV-jobroom.ROLE_JOBSEEKER")
            .put("ROLE_PRIVATE_EMPLOYMENT_AGENT", "ALV-jobroom.ROLE_PRIVATE_EMPLOYMENT_AGENT")
            .put("ROLE_COMPANY", "ALV-jobroom.ROLE_COMPANY")
            .put("ROLE_PUBLIC_EMPLOYMENT_SERVICE", "ALV-jobroom.ROLE_PUBLIC_EMPLOYMENT_SERVICE")
            .put("ROLE_ADMIN", "ALV-jobroom.ROLE_SYSADMIN")
            .put("ROLE_SYSADMIN", "ALV-jobroom.ROLE_SYSADMIN")
            .build();

        EiamRoleMapper eiamRoleMapper = new EiamRoleMapper(mappings);

        //when
        Set<String> jobRoomRoles = eiamRoleMapper.mapEiamRolesToJobRoomRoles(Arrays.asList("ALV-jobroom.ALLOW", "ALV-jobroom.ROLE_SYSADMIN"));

        assertThat(jobRoomRoles)
            .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_SYSADMIN", "ROLE_ALLOW");
    }

}
