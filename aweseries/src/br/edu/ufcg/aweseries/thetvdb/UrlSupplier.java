package br.edu.ufcg.aweseries.thetvdb;

public class UrlSupplier {
	private String apiKey;
	private Mirrors mirrors;

	public UrlSupplier(String apiKey) {
		this.apiKey = apiKey;
	}

	//MIRRORS-------------------------------------------------------------------
	
	private String getMirrorUrl() {
	    return "http://thetvdb.com/api/" + this.apiKey + "/mirrors.xml";
	}

	private void loadMirrors() {
	    MirrorsParser parser = new MirrorsParser(this.getMirrorUrl());
	    this.mirrors = parser.parse();
	}
	
	private String getMirrorPath(MirrorType type) {
	    if (this.mirrors == null) {
	        this.loadMirrors();
	    }
	    return this.mirrors.getRandomMirror(type).getPath();
	}

    private StringBuilder getXmlUrl() {
        return new StringBuilder(this.getMirrorPath(MirrorType.XML))
                .append("/api/").append(this.apiKey);
    }

	private StringBuilder getBannerUrl() {
        return new StringBuilder(this.getMirrorPath(MirrorType.BANNER))
                .append("/banners/");
    }

    @SuppressWarnings("unused")
    private StringBuilder getZipUrl() {
        return new StringBuilder(this.getMirrorPath(MirrorType.ZIP))
                .append("/api/").append(this.apiKey);
    }

    //SERIES ------------------------------------------------------------------
    
    private StringBuilder getBaseSeriesUrlBuilder(String id) {
        return this.getXmlUrl().append("/series/").append(id);
    }

    public String getBaseSeriesUrl(String id) {
        return this.getBaseSeriesUrlBuilder(id).toString();
    }

    public String getFullSeriesUrl(String id) {
        return getBaseSeriesUrlBuilder(id).append("/all/").toString();
    }

    public String getSeriesSearchUrl(String name) {
		return "http://www.thetvdb.com/api/GetSeries.php?seriesname=" + name;
	}

    //POSTERS -----------------------------------------------------------------

    public String getSeriesPosterUrl(String filename) {
    	if (filename == null || filename.trim().isEmpty()) {
    		return null;
    	}
    	return this.getBannerUrl().append(filename).toString();
    }
}