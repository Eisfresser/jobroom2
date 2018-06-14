package ch.admin.seco.jobroom.web.rest.vm;

import java.time.LocalDate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * View Model object for storing a Jobseeker's personal number and birthdate.
 */
public class RegisterJobseekerVM {

    @Digits(integer = 20, fraction = 0)
    @NotNull
    private Long personNumber;

    @NotNull
    private LocalDate birthdate;

    public long getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(long personNumber) {
        this.personNumber = personNumber;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public String toString() {
        return "RegisterJobseekerVM{" +
            "personNumber=" + personNumber +
            ", birthdate=" + birthdate +
            '}';
    }
}
