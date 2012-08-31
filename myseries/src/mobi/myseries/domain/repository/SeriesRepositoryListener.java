package mobi.myseries.domain.repository;

import java.util.Collection;

import mobi.myseries.domain.model.Series;

public interface SeriesRepositoryListener {
    public void onInsert(Series s);
    public void onUpdate(Series s);
    public void onUpdate(Collection<Series> s);
    public void onDelete(Series s);
    public void onDelete(Collection<Series> s);
}