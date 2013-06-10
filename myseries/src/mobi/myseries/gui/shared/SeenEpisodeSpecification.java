package mobi.myseries.gui.shared;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.AbstractSpecification;

public class SeenEpisodeSpecification extends AbstractSpecification<Episode>{

    @Override
    public boolean isSatisfiedBy(Episode e) {
        return e != null && e.wasSeen();
    }
}
