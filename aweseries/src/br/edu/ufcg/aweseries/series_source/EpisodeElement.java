package br.edu.ufcg.aweseries.series_source;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Numbers;
import br.edu.ufcg.aweseries.util.Strings;
import br.edu.ufcg.aweseries.util.Validate;

public class EpisodeElement {
    private static final String ID = "id";
    private static final String SERIES_ID = "seriesid";
    private static final String NUMBER = "EpisodeNumber";
    private static final String SEASON_NUMBER = "SeasonNumber";
    private static final String NAME = "EpisodeName";
    private static final String AIRDATE = "FirstAired";
    private static final String OVERVIEW = "Overview";
    private static final String DIRECTORS = "Director";
    private static final String WRITERS = "Writer";
    private static final String GUEST_STARS = "GuestStars";
    private static final String IMAGE_FILE_NAME = "filename";

    private static final int INVALID_EPISODE_ID = -1;
    private static final int INVALID_SERIES_ID = -1;
    private static final int INVALID_EPISODE_NUMBER = -1;
    private static final int INVALID_SEASON_NUMBER = -1;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Element wrappedElement;
    private Episode.Builder episodeBuilder;

    //Construction------------------------------------------------------------------------------------------------------

    private EpisodeElement(RootElement root) {
        Validate.isNonNull(root, "root");

        this.wrappedElement = root.requireChild("Episode");
        this.episodeBuilder = Episode.builder();
    }

    //Factory-----------------------------------------------------------------------------------------------------------

    public static EpisodeElement from(RootElement root) {
        return new EpisodeElement(root);
    }

    //Wrapped element---------------------------------------------------------------------------------------------------

    public Element wrappedElement() {
        return this.wrappedElement;
    }

    //Content handling--------------------------------------------------------------------------------------------------

    public EpisodeElement withId() {
        this.wrappedElement.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body, INVALID_EPISODE_ID);
                EpisodeElement.this.episodeBuilder.withId(id);
            }
        });

        return this;
    }

    public EpisodeElement withSeriesId() {
        this.wrappedElement.getChild(SERIES_ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int seriesId = Numbers.parseInt(body, INVALID_SERIES_ID);
                EpisodeElement.this.episodeBuilder.withSeriesId(seriesId);
            }
        });

        return this;
    }

    public EpisodeElement withNumber() {
        this.wrappedElement.getChild(NUMBER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int number = Numbers.parseInt(body, INVALID_EPISODE_NUMBER);
                EpisodeElement.this.episodeBuilder.withNumber(number);
            }
        });

        return this;
    }

    public EpisodeElement withSeasonNumber() {
        this.wrappedElement.getChild(SEASON_NUMBER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int seasonNumber = Numbers.parseInt(body, INVALID_SEASON_NUMBER);
                EpisodeElement.this.episodeBuilder.withSeasonNumber(seasonNumber);
            }
        });

        return this;
    }

    public EpisodeElement withName() {
        this.wrappedElement.getChild(NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElement.this.episodeBuilder.withName(body);
            }
        });

        return this;
    }

    public EpisodeElement withAirdate() {
        this.wrappedElement.getChild(AIRDATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Date airdate = Dates.parseDate(body, DATE_FORMAT, null);
                EpisodeElement.this.episodeBuilder.withAirdate(airdate);
            }
        });

        return this;
    }
    
    public EpisodeElement withOverview() {
        this.wrappedElement.getChild(OVERVIEW).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElement.this.episodeBuilder.withOverview(body);
            }
        });
        
        return this;
    }

    public EpisodeElement withDirectors() {
        this.wrappedElement.getChild(DIRECTORS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String directors = Strings.normalizePipeSeparated(body);
                EpisodeElement.this.episodeBuilder.withDirectors(directors);
            }
        });

        return this;
    }

    public EpisodeElement withWriters() {
        this.wrappedElement.getChild(WRITERS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String writers = Strings.normalizePipeSeparated(body);
                EpisodeElement.this.episodeBuilder.withWriters(writers);
            }
        });

        return this;
    }

    public EpisodeElement withGuestStars() {
        this.wrappedElement.getChild(GUEST_STARS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String guestStars = Strings.normalizePipeSeparated(body);
                EpisodeElement.this.episodeBuilder.withGuestStars(guestStars);
            }
        });

        return this;
    }

    public EpisodeElement withImageFileName() {
        this.wrappedElement.getChild(IMAGE_FILE_NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EpisodeElement.this.episodeBuilder.withImageFileName(body);
            }
        });

        return this;
    }

    //Handled content---------------------------------------------------------------------------------------------------

    public Episode handledContent() {
        return this.episodeBuilder.build();
    }
}
