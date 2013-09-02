package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.domain.model.SearchResult;

public interface TrendingListener {
    public void onStart();
    public void onFinish();
    public void onSucess(List<SearchResult> results);
    public void onFailure(Exception failure);
}
