package mobi.myseries.gui.shared;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.AbstractSpecification;

public class EpisodeWatchMarkSpecification extends AbstractSpecification<Episode> {
    private boolean watched;

    public EpisodeWatchMarkSpecification(boolean watched) {
        this.watched = watched;
    }

    @Override
    public boolean isSatisfiedBy(Episode e) {
        return e != null && e.watched() == watched;
    }
}
