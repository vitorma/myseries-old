package mobi.myseries.domain.source;

import java.util.HashMap;
import java.util.Map;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Validate;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

public class PosterUpdateElementHandler {

    private static final String SERIES_ID = "Series";
    private static final String BANNER = "Banner";
    private static final String TYPE = "type";
    private static final String POSTER = "poster";
    private static final String PATH = "path";
    
    private Element seriesElement;
    private int currentResult;
    private String currentResultType;
    private String path;
    
    private Map<Integer, String> results;

    public PosterUpdateElementHandler(RootElement rootElement) {
        Validate.isNonNull(rootElement, "rootElement");

        this.seriesElement = rootElement.getChild(BANNER);
        this.results = new HashMap<Integer, String>();

        this.storeTheCurrentResultAtTheEndOfEachSeriesElement();
    }

    private void storeTheCurrentResultAtTheEndOfEachSeriesElement() {
        this.seriesElement.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                if (POSTER.equals(currentResultType)) {
                PosterUpdateElementHandler.this.results.put(currentResult, path);
                } else {
                }
            }
        });        
    }

    public static PosterUpdateElementHandler from(RootElement rootElement) {
        return new PosterUpdateElementHandler(rootElement);
    }

    public PosterUpdateElementHandler handlingSeriesId() {
        this.seriesElement.getChild(SERIES_ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body, Invalid.SERIES_ID);
                PosterUpdateElementHandler.this.currentResult = id;
            }
        });

        return this;
    }
    
    public PosterUpdateElementHandler handlingPosterPath() {
        this.seriesElement.getChild(PATH).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                PosterUpdateElementHandler.this.path = body.trim();
            }
        });

        return this;
    }


    public PosterUpdateElementHandler handlingImageType() {
        this.seriesElement.getChild(TYPE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                PosterUpdateElementHandler.this.currentResultType = body.trim();
            }
        });
        
        return this;
    }


    public Integer currentResult() {
        return this.currentResult;
    }

    public Map<Integer, String> allResults() {
        return this.results;
    }

}
