package ch.admin.seco.jobroom.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ch.admin.seco.jobroom.domain.SystemNotification;


public class SystemNotificationDTO {

    private UUID id;

    @NotNull
    @Size(max = 50)
    private String title;

    @NotNull
    @Size(max = 150)
    private String text_de;

    @NotNull
    @Size(max = 150)
    private String text_fr;

    @NotNull
    @Size(max = 150)
    private String text_it;

    @NotNull
    @Size(max = 150)
    private String text_en;

    @NotNull
    @Size(max = 50)
    private String type;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean isActive;

    public SystemNotificationDTO() {
        // Empty constructor needed for Jackson.
    }

    public SystemNotificationDTO(UUID id, String title, String text_de, String text_fr, String text_it, String text_en, String type, LocalDateTime startDate, LocalDateTime endDate, boolean isActive) {
        this.id = id;
        this.title = title;
        this.text_de = text_de;
        this.text_fr = text_fr;
        this.text_it = text_it;
        this.text_en = text_en;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText_de() {
        return text_de;
    }

    public void setText_de(String text_de) {
        this.text_de = text_de;
    }

    public String getText_fr() {
        return text_fr;
    }

    public void setText_fr(String text_fr) {
        this.text_fr = text_fr;
    }

    public String getText_it() {
        return text_it;
    }

    public void setText_it(String text_it) {
        this.text_it = text_it;
    }

    public String getText_en() {
        return text_en;
    }

    public void setText_en(String text_en) {
        this.text_en = text_en;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static SystemNotificationDTO toDto(SystemNotification systemNotification) {
        SystemNotificationDTO systemNotificationDto = new SystemNotificationDTO();
        systemNotificationDto.setId(systemNotification.getId());
        systemNotificationDto.setTitle(systemNotification.getTitle());
        systemNotificationDto.setText_de(systemNotification.getText_de());
        systemNotificationDto.setText_fr(systemNotification.getText_fr());
        systemNotificationDto.setText_it(systemNotification.getText_it());
        systemNotificationDto.setText_en(systemNotification.getText_en());
        systemNotificationDto.setType(systemNotification.getType());
        systemNotificationDto.setStartDate(systemNotification.getStartDate());
        systemNotificationDto.setEndDate(systemNotification.getEndDate());
        systemNotificationDto.setActive(systemNotification.isActive());
        return systemNotificationDto;
    }
}
