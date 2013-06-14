package mobi.myseries.gui.shared;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.DatesAndTimes;

public class UnairedEpisodeSpecification extends AbstractSpecification<Episode> {

    @Override
    public boolean isSatisfiedBy(Episode e) {
        return e != null && DatesAndTimes.compareByNullLast(e.airDate(), DatesAndTimes.now()) > 0;
    }
}
