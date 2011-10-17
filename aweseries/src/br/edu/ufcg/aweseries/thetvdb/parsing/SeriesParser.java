package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.IOException;

import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesBuilder;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.util.Strings;

public class SeriesParser {
    private StreamFactory streamFactory;

    public SeriesParser(StreamFactory streamFactory) {
        if (streamFactory == null) {
            throw new IllegalArgumentException("streamFactory should not be null");
        }

        this.streamFactory = streamFactory;
    }

    public Series parse(String seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }
        if (Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("seriesId should not be blank");
        }

        final SeriesBuilder builder = new SeriesBuilder();
        final RootElement root = new RootElement("Data");
        final Element element = root.getChild("Series");

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

        element.getChild("Status").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withStatus(body);
                    }
                });

        element.getChild("Airs_DayOfWeek").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withAirsDay(body);
                    }
                });

        element.getChild("Airs_Time").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withAirsTime(body);
                    }
                });

        element.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withFirstAired(body);
                    }
                });

        element.getChild("Runtime").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withRuntime(body);
                    }
                });

        element.getChild("Network").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withNetwork(body);
                    }
                });

        element.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withOverview(body);
                    }
                });

        element.getChild("Genre").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withGenres(Strings.normalizePipeSeparated(body));
                    }
                });

        element.getChild("Actors").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withActors(Strings.normalizePipeSeparated(body));
                    }
                });

        element.getChild("poster").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withPoster((Bitmap) SeriesParser.this.scaledBitmapFrom(body));
                    }
                });

        try {
            Xml.parse(this.streamFactory.streamForFullSeries(seriesId),
                    Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }

    private Bitmap scaledBitmapFrom(String resourcePath) {
        return Strings.isBlank(resourcePath)
               ? null
               : BitmapFactory.decodeStream(
                       this.streamFactory.streamForSeriesPosterAt(resourcePath));
    }
}
