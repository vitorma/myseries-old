package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.domain.model.SearchResult;

public interface SearchListener {
    public void onStart();
    public void onFinish();
    public void onSucess(List<SearchResult> list);
    public void onFailure(Exception exception);
}
