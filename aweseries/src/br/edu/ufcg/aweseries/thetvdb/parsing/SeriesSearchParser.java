package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesBuilder;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.util.Strings;

public class SeriesSearchParser {
    private StreamFactory streamFactory;

    public SeriesSearchParser(StreamFactory streamFactory) {
        if (streamFactory == null) {
            throw new IllegalArgumentException("streamFactory should not be null");
        }

        this.streamFactory = streamFactory;
    }

    public List<Series> parse(String seriesName) {
        if (seriesName == null) {
            throw new IllegalArgumentException("seriesName should not be null");
        }
        if (Strings.isBlank(seriesName)) {
            throw new IllegalArgumentException("seriesName should not be blank");
        }

        final List<Series> searchResult = new ArrayList<Series>();
        final SeriesBuilder builder = new SeriesBuilder();

        final RootElement root = new RootElement("Data");
        final Element element = root.getChild("Series");

        element.setEndElementListener(
                new EndElementListener() {
                    @Override
                    public void end() {
                        searchResult.add(builder.build());
                    }
                });

        element.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withId(body);
                    }
                });

        element.getChild("SeriesName").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withName(body);
                    }
                });

        element.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withOverview(body);
                    }
                });

        try {
            Xml.parse(this.streamFactory.streamForSeriesSearch(seriesName),
                    Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return Collections.unmodifiableList(searchResult);
    }
}
