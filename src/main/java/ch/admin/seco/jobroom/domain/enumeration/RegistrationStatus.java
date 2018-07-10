package ch.admin.seco.jobroom.domain.enumeration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public enum RegistrationStatus {
    UNREGISTERED,
    REGISTERED,
    VALIDATION_EMP,
    VALIDATION_PAV;
    private static final Map<RegistrationStatus, Set<RegistrationStatus>> VALID_TRANSITIONS = new HashMap<>();

    static {
        VALID_TRANSITIONS.put(UNREGISTERED, Sets.newHashSet(REGISTERED, VALIDATION_EMP, VALIDATION_PAV));
        VALID_TRANSITIONS.put(VALIDATION_EMP, Sets.newHashSet(REGISTERED, UNREGISTERED));
        VALID_TRANSITIONS.put(VALIDATION_PAV, Sets.newHashSet(REGISTERED, UNREGISTERED));
        VALID_TRANSITIONS.put(REGISTERED, Sets.newHashSet(UNREGISTERED));
    }

    RegistrationStatus() {
    }

    public boolean canChangeTo(RegistrationStatus newRegistrationStatus) {
        return VALID_TRANSITIONS.get(this).contains(newRegistrationStatus);
    }
}
