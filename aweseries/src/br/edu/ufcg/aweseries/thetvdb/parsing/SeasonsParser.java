//<?xml version="1.0" encoding="UTF-8" ?>
// <Episode>
//    <id>332179</id>
//    <DVD_chapter></DVD_chapter>
//    <DVD_discid></DVD_discid>
//    <DVD_episodenumber></DVD_episodenumber>
//    <DVD_season></DVD_season>
//    <Director>|Joseph McGinty Nichol|</Director>
//    <EpisodeName>Chuck Versus the World</EpisodeName>
//    <EpisodeNumber>1</EpisodeNumber>
//    <FirstAired>2007-09-24</FirstAired>
//    <GuestStars>|Julia Ling|Vik Sahay|Mieko Hillman|</GuestStars>
//    <IMDB_ID></IMDB_ID>
//    <Language>English</Language>
//    <Overview>Chuck Bartowski is an average computer geek...</Overview>
//    <ProductionCode></ProductionCode>
//    <Rating>9.0</Rating>
//    <SeasonNumber>1</SeasonNumber>
//    <Writer>|Josh Schwartz|Chris Fedak|</Writer>
//    <absolute_number></absolute_number>
//    <airsafter_season></airsafter_season>
//    <airsbefore_episode></airsbefore_episode>
//    <airsbefore_season></airsbefore_season>
//    <filename>episodes/80348-332179.jpg</filename>
//    <lastupdated>1201292806</lastupdated>
//    <seasonid>27985</seasonid>
//    <seriesid>80348</seriesid>
//</Episode>

package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.InputStream;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;
import br.edu.ufcg.aweseries.model.EpisodeBuilder;
import br.edu.ufcg.aweseries.model.Seasons;

public class SeasonsParser extends TheTVDBParser<Seasons> {
	
	public SeasonsParser(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public Seasons parse() {
		final Seasons seasons = new Seasons();
		final EpisodeBuilder builder = new EpisodeBuilder();

		RootElement root = new RootElement("Data");
		Element element = root.getChild("Episode");

		element.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				seasons.addEpisode(builder.build());
			}
		});

		element.getChild("id").setEndTextElementListener(new EndTextElementListener() {
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

        element.getChild("SeasonNumber").setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				builder.withSeasonNumber(body);
			}
		});

        try {
            Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, 
                    root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return seasons;
	}
}
