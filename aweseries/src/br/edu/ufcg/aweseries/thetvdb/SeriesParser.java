//<?xml version="1.0" encoding="UTF-8" ?>
//<Series>
//   <id>80348</id>
//   <Actors>|Zachary Levi|Adam Baldwin|Yvonne Strzechowski|</Actors>
//   <Airs_DayOfWeek>Monday</Airs_DayOfWeek>
//   <Airs_Time>8:00 PM</Airs_Time>
//   <FirstAired>2007-09-24</FirstAired>
//   <Genre>|Comedy|</Genre>
//   <IMDB_ID>tt0934814</IMDB_ID>
//   <Language>English</Language>
//   <Network>NBC</Network>
//   <Overview>Zachary Levi (Less Than Perfect) plays Chuck...</Overview>
//   <Rating>9.0</Rating>
//   <Runtime>30 mins</Runtime>
//   <SeriesID>68724</SeriesID>
//   <SeriesName>Chuck</SeriesName>
//   <Status>Continuing</Status>
//   <lastupdated>1200785226</lastupdated>
// </Series>

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

package br.edu.ufcg.aweseries.thetvdb;

import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public final class SeriesParser extends TheTVDBParser<Series> {

    public SeriesParser(String url) {
        super(url);
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

        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, 
                    root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return series;
    }
}
