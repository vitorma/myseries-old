package mobi.myseries.shared;

import java.util.Date;

public interface HasDate {

    public Date getDate();

    public boolean hasSameDateAs(HasDate other);
}
