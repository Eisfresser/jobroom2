package ch.admin.seco.jobroom.web.rest.vm;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * View Model object for storing a Jobseeker's personal number and birthdate.
 */
public class RegisterJobseekerVM {

    @Digits(integer = 20, fraction = 0)
    @NotNull
    private Long personNumber;

    @Digits(integer = 4, fraction = 0)
    @DecimalMax("2999")
    @DecimalMin("1900")
    @NotNull
    private Integer birthdateYear;

    @Digits(integer = 2, fraction = 0)
    @DecimalMax("12")
    @DecimalMin("1")
    @NotNull
    private Integer birthdateMonth;

    @Digits(integer = 2, fraction = 0)
    @DecimalMax("31")
    @DecimalMin("1")
    @NotNull
    private Integer birthdateDay;

    public long getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(long personNumber) {
        this.personNumber = personNumber;
    }

    public Integer getBirthdateYear() {
        return birthdateYear;
    }

    public void setBirthdateYear(Integer birthdateYear) {
        this.birthdateYear = birthdateYear;
    }

    public Integer getBirthdateMonth() {
        return birthdateMonth;
    }

    public void setBirthdateMonth(Integer birthdateMonth) {
        this.birthdateMonth = birthdateMonth;
    }

    public Integer getBirthdateDay() {
        return birthdateDay;
    }

    public void setBirthdateDay(Integer birthdateDay) {
        this.birthdateDay = birthdateDay;
    }

    @Override
    public String toString() {
        return "RegisterJobseekerVM{" +
            "personNumber=" + personNumber +
            ", birthdateYear=" + birthdateYear +
            ", birthdateMonth=" + birthdateMonth +
            ", birthdateDay=" + birthdateDay +
            '}';
    }
}
