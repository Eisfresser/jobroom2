package ch.admin.seco.jobroom.service.impl.messaging;

import java.io.Serializable;
import java.util.UUID;

class CandidateDeletedEvent implements Serializable {

    static CandidateDeletedEvent from(UUID candidateId, Long personNumber) {
        CandidateDeletedEvent event = new CandidateDeletedEvent();
        event.setPersonNumber(personNumber);
        event.setCandidateId(candidateId);
        return event;
    }

    private UUID candidateId;

    private Long personNumber;

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public Long getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(Long personNumber) {
        this.personNumber = personNumber;
    }
}
