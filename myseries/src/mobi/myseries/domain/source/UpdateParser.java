package mobi.myseries.domain.source;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.Set;

import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

import mobi.myseries.shared.Validate;

public class UpdateParser {

    private InputStream streamForUpdate;
    private Map<Integer, String> parsedPosters;
    private Set<Integer> parsedSeries;

    public UpdateParser(InputStream streamForUpdate) {
        Validate.isNonNull(streamForUpdate, "inputStream");

        this.streamForUpdate = streamForUpdate;
    }
    
    private static class Content {
        private static final String DATA = "Data";
        
        private RootElement rootElement = new RootElement(DATA);

        private SeriesUpdateElementHandler seriesElementHandler;
        private EpisodeUpdateElementHandler episodeElementHandler;
        private PosterUpdateElementHandler posterElementHandler;

        public Content() {
            this.seriesElementHandler = SeriesUpdateElementHandler.from(this.rootElement)
                    .handlingId();
            
            this.episodeElementHandler = EpisodeUpdateElementHandler.from(this.rootElement)
                    .handlingSeriesId();
            
            this.posterElementHandler = PosterUpdateElementHandler.from(this.rootElement)
                    .handlingImageType().handlingSeriesId().handlingPosterPath();
        }
        
        public ContentHandler handler() {
            return this.rootElement.getContentHandler();
        }
        
        public Set<Integer> handledSeries() {
            Set<Integer> series = this.seriesElementHandler.allResults();

            for (int seriesId: this.episodeElementHandler.allResults()) {
                series.add(seriesId);
            }            
            
            return series;
        }
        
        public Map<Integer, String> handledPosters() {
            Map<Integer, String> posters = this.posterElementHandler.allResults();

            return posters;
        }
    }

    public boolean parse() throws ConnectionFailedException,
            ParsingFailedException {
        InputStream stream = this.streamForUpdate;
        
        StringBuffer buf = new StringBuffer();

        Content content = new Content();
        
        try {
            Log.d(getClass().getName(), buf.toString());
            Xml.parse(stream, Encoding.UTF_8, content.handler());
        } catch (SAXException e) {
            throw new ParsingFailedException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParsingFailedException(e);
        }
        
        Log.d(getClass().getName(), "series read: " + content.handledSeries().size());

        this.parsedPosters = content.handledPosters();
        this.parsedSeries = content.handledSeries();
        
        return (this.parsedPosters.size() > 0) || (this.parsedSeries.size() > 0);
    }

    public Map<Integer, String> parsedPosters() {
        return parsedPosters;
    }
    
    public Set<Integer> parsedSeries() {
        return parsedSeries;
    }
}
