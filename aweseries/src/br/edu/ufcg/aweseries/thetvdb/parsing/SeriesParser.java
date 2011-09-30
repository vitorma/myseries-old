package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesBuilder;
import br.edu.ufcg.aweseries.util.Strings;

public class SeriesParser extends TheTVDBParser<Series> {

    public SeriesParser(InputStream seriesInputStream) {
        super(seriesInputStream);
    }

    @Override
    public Series parse() {
        final SeriesBuilder builder = new SeriesBuilder();

        final RootElement root = new RootElement("Data");
        final Element element = root.getChild("Series");

        element.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withId(body);
                    }
                });

        element.getChild("SeriesName").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withName(body);
                    }
                });

        element.getChild("Status").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withStatus(body);
                    }
                });

        element.getChild("Airs_DayOfWeek").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withAirsDay(body);
                    }
                });

        element.getChild("Airs_Time").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withAirsTime(body);
                    }
                });

        element.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withFirstAired(body);
                    }
                });

        element.getChild("Runtime").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withRuntime(body);
                    }
                });

        element.getChild("Network").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withNetwork(body);
                    }
                });

        element.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withOverview(body);
                    }
                });

        element.getChild("Genre").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withGenres(Strings.normalizePipeSeparated(body));
                    }
                });

        element.getChild("Actors").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withActors(Strings.normalizePipeSeparated(body));
                    }
                });

        element.getChild("poster").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        builder.withPoster(body);
                    }
                });

        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}
