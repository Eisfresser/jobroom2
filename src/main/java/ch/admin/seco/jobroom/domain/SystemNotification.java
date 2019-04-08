package ch.admin.seco.jobroom.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;





@Entity
@Table(name = "systemnotification")
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


    @Column(name = "type")
    private String type;


    @Column(name = "startdate")
    private LocalDateTime startDate;

    @Column(name = "enddate")
    private LocalDateTime endDate;

    @Column(name = "active")
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

    public String getType() {
        return type;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SystemNotification systemNotification = (SystemNotification) o;
        return !(systemNotification.getId() == null || getId() == null) && Objects.equals(getId(), systemNotification.getId());
    }

    @Override
    public String toString() {
        return "SystemNotification{" +
            "title='" + title + '\'' +
            ", text_de='" + text_de + '\'' +
            ", text_fr='" + text_fr + '\'' +
            ", text_it='" + text_it + '\'' +
            ", text_en='" + text_en + '\'' +
            ", type='" + type + '\'' +
            ", startDate='" + startDate + '\'' +
            ", endDate='" + endDate + '\'' +
            ", isActive='" + isActive + '\'' +
            "}";
    }
}

