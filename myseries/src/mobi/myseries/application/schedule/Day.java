package mobi.myseries.application.schedule;

import java.util.Date;

import mobi.myseries.shared.HasDate;
import mobi.myseries.shared.Validate;

public class Day implements HasDate {
    private Date date;

    public Day(Date date) {
        Validate.isNonNull(date, "date");

        this.date = date;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    @Override
    public boolean hasSameDateAs(HasDate other) {
        return other != null && this.getDate().equals(other.getDate());
    }

    @Override
    public int hashCode() {
        return this.date.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == Day.class && this.date.equals(((Day) obj).date);
    }
}
