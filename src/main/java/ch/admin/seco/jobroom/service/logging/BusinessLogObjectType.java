package ch.admin.seco.jobroom.service.logging;

public enum BusinessLogObjectType {
    CANDIDATE("Candidate"), USER("User");

    private String typeName;

    BusinessLogObjectType(String typeName) {
        this.typeName = typeName;
    }

    public String typeName() {
        return typeName;
    }
}
