package mobi.myseries.application.trending;

import java.util.List;

import mobi.myseries.domain.model.ParcelableSeries;

public interface TrendingListener {
    public void onStart();
    public void onFinish();
    public void onSucess(List<ParcelableSeries> results);
    public void onFailure(Exception failure);
}
