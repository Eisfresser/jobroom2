package ch.admin.seco.jobroom.domain.enumeration;

public enum RegistrationStatus {
    UNREGISTERED,   // user is authenticated through eIAM, but still has to register in Jobroom
    VALIDATION_EMP, // user started Jobroom registration as employer, but did not enter the access code sent by post yet
    VALIDATION_PAV, // user started Jobroom registration as PRIVATE_AGENT, but did not enter the access code sent by post yet
    REGISTERED      // user is fully registered in Jobroom and got the application role suiting his user type
}
