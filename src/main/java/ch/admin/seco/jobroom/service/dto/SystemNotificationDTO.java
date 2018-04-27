package ch.admin.seco.jobroom.service.dto;


import ch.admin.seco.jobroom.domain.SystemNotification;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public class SystemNotificationDTO {

    @NotBlank
    @Size(min = 1, max = 50)
    private UUID id;

    @Size(max = 50)
    private String title;

    @Size(max = 50)
    private String text;

    @Size(max = 50)
    private String type;

    @Size(max = 50)
    private LocalDateTime startDate;

    @Size(max = 50)
    private LocalDateTime endDate;

    @Size(max = 50)
    private boolean isActive;

    public SystemNotificationDTO() {
        // Empty constructor needed for Jackson.
    }

    public SystemNotificationDTO(UUID id, String title, String text, String type, LocalDateTime startDate, LocalDateTime endDate, boolean isActive) {
        this.id = id;
        this.title = title;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
        systemNotificationDto.setText(systemNotification.getText());
        systemNotificationDto.setType(systemNotification.getTitle());
        systemNotificationDto.setStartDate(systemNotification.getStartDate());
        systemNotificationDto.setEndDate(systemNotification.getEndDate());
        systemNotificationDto.setActive(systemNotification.isActive());
        return systemNotificationDto;
    }
}
