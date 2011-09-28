/*
<?xml version="1.0" encoding="UTF-8" ?>
<Data>
  <Series>
    <id>80348</id>
    <Actors>|Zachary Levi|Yvonne Strahovski|Adam Baldwin|Bonita Friedericy|Julia Ling|Vik Sahay|Ryan McPartlin|Scott Krinsky|Mark Christopher Lawrence|Sarah Lancaster|Joshua Gomez|Mekenna Melvin|Linda Hamilton|Brandon Routh|Matthew Bomer|</Actors>
    <Airs_DayOfWeek>Monday</Airs_DayOfWeek>
    <Airs_Time>8:00 PM</Airs_Time>
    <ContentRating>TV-PG</ContentRating>
    <FirstAired>2007-09-24</FirstAired>
    <Genre>|Action and Adventure|Comedy|Drama|</Genre>
    <IMDB_ID>tt0934814</IMDB_ID>
    <Language>en</Language>
    <Network>NBC</Network>
    <NetworkID></NetworkID>
    <Overview>Chuck Bartowski, ace computer geek at Buy More, is not in his right mind. That's a good thing. Ever since he unwittingly downloaded stolen government secrets into his brain, action, excitement and a cool secret- agent girlfriend have entered his life. It's a bad thing, too. Because now Chuck is in danger 24/7.</Overview>
    <Rating>8.8</Rating>
    <RatingCount>654</RatingCount>
    <Runtime>60</Runtime>
    <SeriesID>68724</SeriesID>
    <SeriesName>Chuck</SeriesName>
    <Status>Continuing</Status>
    <added></added>
    <addedBy></addedBy>
    <banner>graphical/80348-g21.jpg</banner>
    <fanart>fanart/original/80348-18.jpg</fanart>
    <lastupdated>1315862490</lastupdated>
    <poster>posters/80348-15.jpg</poster>
    <zap2it_id>EP00930779</zap2it_id>
  </Series>
</Data>
*/

//<?xml version="1.0" encoding="UTF-8" ?>
//<Data>
// <Series>
//    ALL INFORMATION FROM THE <Series> TAG INSIDE THE Base Series Record WILL BE LISTED HERE.
// </Series>
// <Episode>
//    ALL INFORMATION FROM THE <Episode> TAG INSIDE THE Base Episode Record WILL BE LISTED HERE.
// </Episode>
// <Episode>
//    ALL INFORMATION FROM THE <Episode> TAG INSIDE THE Base Episode Record WILL BE LISTED HERE.
// </Episode>
// <Episode>
//    ALL INFORMATION FROM THE <Episode> TAG INSIDE THE Base Episode Record WILL BE LISTED HERE.
// </Episode>
//</Data> 

package br.edu.ufcg.aweseries.thetvdb.series;

import java.io.InputStream;

import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.thetvdb.TheTVDBParser;
import br.edu.ufcg.aweseries.util.Strings;

public final class SeriesParser extends TheTVDBParser<Series> {

    public SeriesParser(InputStream seriesInputStream) {
        super(seriesInputStream);
    }

    @Override
    public Series parse() {
        final Series series = new Series();

        RootElement root = new RootElement("Data");
        Element element = root.getChild("Series");

        element.getChild("id").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setId(body);
                    }
                }
        );

        element.getChild("SeriesName").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setName(body);
                    }
                }
        );

        element.getChild("Status").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setStatus(body);
                    }
                }
        );

        element.getChild("Overview").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setOverview(body);
                    }
                }
        );

        element.getChild("Genre").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setGenres(Strings.normalizePipeSeparated(body));
                    }
                }
        );

        element.getChild("Actors").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setActors(Strings.normalizePipeSeparated(body));
                    }
                });

        element.getChild("Airs_DayOfWeek").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setAirsDay(body);
                    }
                }
        );

        element.getChild("Airs_Time").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setAirsTime(body);
                    }
                }
        );

        element.getChild("FirstAired").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setFirstAired(body);
                    }
                }
        );

        element.getChild("Runtime").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setRuntime(body);
                    }
                }
        );

        element.getChild("Network").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setNetwork(body);
                    }
                }
        );

        element.getChild("poster").setEndTextElementListener(
                new EndTextElementListener() {
                    public void end(String body) {
                        series.setPoster(body);
                    }
                }
        );

        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, 
                    root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return series;
    }
}
