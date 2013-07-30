package mobi.myseries.domain.source;

import java.io.IOException;
import java.io.InputStream;

import mobi.myseries.domain.source.SeriesBannerElementHandler.FirstPosterFoundInterruption;
import mobi.myseries.shared.Validate;

import org.xml.sax.SAXException;

import android.sax.RootElement;
import android.util.Xml;

public class SeriesBannersParser {
    private StreamFactory streamFactory;

    public SeriesBannersParser(StreamFactory streamFactory) {
        Validate.isNonNull(streamFactory, "streamFactory");

        this.streamFactory = streamFactory;
    }

    public String parse(int seriesId)
            throws ParsingFailedException, StreamCreationFailedException, ConnectionFailedException, ConnectionTimeoutException {
        InputStream stream = this.streamFactory.streamForSeriesBanners(seriesId);

        RootElement rootElement = new RootElement("Banners");
        new SeriesBannerElementHandler(rootElement).lookForFirstPoster();

        try {
            Xml.parse(stream, Xml.Encoding.UTF_8, rootElement.getContentHandler());
        } catch (IOException e) {
            throw new ParsingFailedException(e);
        } catch (SAXException e) {
            throw new ParsingFailedException(e);
        } catch (FirstPosterFoundInterruption i) {
            return i.firstPosterPath();
        }

        return "";
    }
}
