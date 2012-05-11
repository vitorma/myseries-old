package mobi.myseries.gui.schedule;

import java.util.Comparator;
import java.util.List;

import mobi.myseries.domain.model.Episode;

public interface EpisodeListFactory {

    public List<Episode> episodes();

    public Comparator<Episode> episodesComparator();

}
