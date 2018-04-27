package ch.admin.seco.jobroom.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "systemnotification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SystemNotification {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "title", length = 50)
    private String title;

    @NotNull
    @Size(max = 50)
    @Column(name = "text", length = 50)
    private String text;

    @NotNull
    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "startdate", length = 50)
    private LocalDateTime startDate;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "enddate", length = 50)
    private LocalDateTime endDate;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "active", length = 50)
    private boolean isActive;


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
}

