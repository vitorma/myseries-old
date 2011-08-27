package br.edu.ufcg.aweseries.thetvdb;

public class TheTVDB {
    private final UrlSupplier urlSupplier;

    public TheTVDB(String apiKey) {
        this.urlSupplier = new UrlSupplier(apiKey);
    }

    public Series getSeries(int id) {
        String url = this.urlSupplier.getBaseSeriesUrl(id);
        return new SeriesParser(url).parse();
    }
}
