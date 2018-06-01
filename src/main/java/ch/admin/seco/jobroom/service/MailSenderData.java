package ch.admin.seco.jobroom.service;

import java.util.Map;

public class MailSenderData {
    private String subject;

    private String to;

    private Map<String, Object> context;

    public MailSenderData() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
