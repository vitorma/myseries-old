package mobi.myseries.application.schedule;

import java.util.Collection;

import mobi.myseries.domain.model.Episode;

public interface RecentListener {
    public void onMarkAsSeen(Episode e);
    public void onMarkAsNotSeen(Episode e);
    public void onRemove(Collection<Episode> e);
    public void onAdd(Collection<Episode> e);
}
