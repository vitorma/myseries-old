package br.edu.ufcg.aweseries.thetvdb.episode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.thetvdb.TheTVDBParser;

public class EpisodesParser extends TheTVDBParser<List<Episode>> {

    protected EpisodesParser(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public List<Episode> parse() {
        final List<Episode> episodes = new ArrayList<Episode>();
        final EpisodeBuilder builder = new EpisodeBuilder();

        RootElement root = new RootElement("Data");
        Element element = root.getChild("Episode");

        element.setEndElementListener(
                new EndElementListener() {
                    @Override
                    public void end() {
                        episodes.add(builder.build());
                    }
                });

        element.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withId(body);
                    }
                });

        element.getChild("seriesid").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withSeriesId(body);
                    }
                });

        element.getChild("EpisodeNumber").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withNumber(body);
                    }
                });

        element.getChild("SeasonNumber").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withSeasonNumber(body);
                    }
                });

        element.getChild("EpisodeName").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withName(body);
                    }
                });

        element.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withFirstAired(body);
                    }
                });

        element.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withOverview(body);
                    }
                });

        element.getChild("Director").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withDirector(body);
                    }
                });

        element.getChild("Writer").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withWriter(body);
                    }
                });

        element.getChild("GuestStars").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
                    public void end(String body) {
                        builder.withGuestStars(body);
                    }
                });

        element.getChild("filename").setEndTextElementListener(
                new EndTextElementListener() {
                    @Override
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

        return episodes;
    }
}
