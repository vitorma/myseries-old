package mobi.myseries.domain.source;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

import mobi.myseries.shared.Validate;

public class UpdateParser {

    private InputStream streamForUpdate;

    public UpdateParser(InputStream streamForUpdate) {
        Validate.isNonNull(streamForUpdate, "inputStream");

        this.streamForUpdate = streamForUpdate;
    }
    
    private static class Content {
        private static final String DATA = "Data";
        
        private RootElement rootElement = new RootElement(DATA);

        private SeriesUpdateElementHandler seriesElementHandler;
        private EpisodeUpdateElementHandler episodeElementHandler;

        public Content() {
            this.seriesElementHandler = SeriesUpdateElementHandler.from(this.rootElement)
                    .handlingId();
            
            this.episodeElementHandler = EpisodeUpdateElementHandler.from(this.rootElement)
                    .handlingSeriesId();
        }
        
        public ContentHandler handler() {
            return this.rootElement.getContentHandler();
        }
        
        public Set<Integer> handled() {
            Set<Integer> series = this.seriesElementHandler.allResults();

            for (int seriesId: this.episodeElementHandler.allResults()) {
                series.add(seriesId);
            }
            
            return series;
        }
    }

    public Set<Integer> parse() throws ConnectionFailedException,
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
        
        Log.d(getClass().getName(), "series read: " + content.handled().size());
        
        return content.handled();
    }

}
