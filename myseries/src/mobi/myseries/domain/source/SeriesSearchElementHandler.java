package mobi.myseries.domain.source;

import android.sax.EndTextElementListener;
import android.sax.RootElement;

public class SeriesSearchElementHandler extends SeriesElementHandler {
    private static final String POSTER_FILE_NAME = "banner";

    private SeriesSearchElementHandler(RootElement rootElement) {
        super(rootElement);
    }

    public static SeriesSearchElementHandler from(RootElement rootElement) {
        return new SeriesSearchElementHandler(rootElement);
    }

    @Override
    public SeriesElementHandler handlingPosterFileName() {
        this.seriesElement.getChild(POSTER_FILE_NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesSearchElementHandler.this.seriesBuilder.withPosterFileName(SeriesSearchElementHandler.this.posterUrlFrom(body));
            }
        });

        return this;
    }

    private String posterUrlFrom(String posterFileName) {
        try {
            return new UrlFactory(TheTVDBConstants.API_KEY).urlForSeriesPoster(posterFileName).toExternalForm();
        } catch (Exception e) {
            return "";
        }
    }
}
