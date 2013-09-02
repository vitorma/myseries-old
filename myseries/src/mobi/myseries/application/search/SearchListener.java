package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.domain.model.ParcelableSeries;

public interface SearchListener {
    public void onStart();
    public void onFinish();
    public void onSucess(List<ParcelableSeries> list);
    public void onFailure(Exception exception);
}
