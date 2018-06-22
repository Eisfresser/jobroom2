package ch.admin.seco.jobroom.security.saml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class EiamRoleMapper {

    private final Map<String, String> rolemapping;

    EiamRoleMapper(Map<String, String> rolemapping) {
        this.rolemapping = rolemapping;
    }

    Set<String> mapEiamRolesToJobRoomRoles(List<String> eiamRoles) {
        Map<String, List<String>> multimap = new HashMap<>();
        for (Map.Entry<String, String> mappingEntry : this.rolemapping.entrySet()) {
            String jobRoomRole = mappingEntry.getKey();
            String eiamRole = mappingEntry.getValue();
            multimap.computeIfAbsent(eiamRole, k -> new ArrayList<>())
                .add(jobRoomRole);
        }
        Set<String> result = new HashSet<>();
        for (String eiamRole : eiamRoles) {
            List<String> strings = multimap.getOrDefault(eiamRole, Collections.emptyList());
            result.addAll(strings);
        }
        return result;
    }

}
