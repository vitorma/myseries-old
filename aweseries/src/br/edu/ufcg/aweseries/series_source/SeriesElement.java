package br.edu.ufcg.aweseries.series_source;

import java.io.InputStream;
import java.util.Collection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Numbers;
import br.edu.ufcg.aweseries.util.Strings;
import br.edu.ufcg.aweseries.util.Validate;

public class SeriesElement {
    private static final String SERIES = "Series";
    private static final String ID = "id";
    private static final String NAME = "SeriesName";
    private static final String STATUS = "Status";
    private static final String AIR_DAY = "Airs_DayOfWeek";
    private static final String AIR_TIME = "Airs_Time";
    private static final String AIRDATE = "FirstAired";
    private static final String RUNTIME = "Runtime";
    private static final String NETWORK = "Network";
    private static final String OVERVIEW = "Overview";
    private static final String GENRES = "Genre";
    private static final String ACTORS = "Actors";
    private static final String POSTER = "poster";

    private Element wrappedElement;
    private Series.Builder seriesBuilder;

    //Construction------------------------------------------------------------------------------------------------------

    private SeriesElement(RootElement root) {
        Validate.isNonNull(root, "root");

        this.wrappedElement = root.requireChild(SERIES);
        this.seriesBuilder = new Series.Builder();
    }

    //Factory-----------------------------------------------------------------------------------------------------------

    public static SeriesElement from(RootElement root) {
        return new SeriesElement(root);
    }

    //Content handling--------------------------------------------------------------------------------------------------

    public SeriesElement withId() {
        this.wrappedElement.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body, Series.INVALID_ID);
                SeriesElement.this.seriesBuilder.withId(id);
            }
        });

        return this;
    }

    public SeriesElement withName() {
        this.wrappedElement.getChild(NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withName(body);
            }
        });

        return this;
    }

    public SeriesElement withStatus() {
        this.wrappedElement.getChild(STATUS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withStatus(body);
            }
        });

        return this;
    }

    public SeriesElement withAirDay() {
        this.wrappedElement.getChild(AIR_DAY).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withAirsDay(body);
            }
        });

        return this;
    }

    public SeriesElement withAirTime() {
        this.wrappedElement.getChild(AIR_TIME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withAirsTime(body);
            }
        });

        return this;
    }

    public SeriesElement withAirdate() {
        this.wrappedElement.getChild(AIRDATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withFirstAired(body);
            }
        });

        return this;
    }

    public SeriesElement withRuntime() {
        this.wrappedElement.getChild(RUNTIME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withRuntime(body);
            }
        });

        return this;
    }

    public SeriesElement withNetwork() {
        this.wrappedElement.getChild(NETWORK).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withNetwork(body);
            }
        });

        return this;
    }

    public SeriesElement withOverview() {
        this.wrappedElement.getChild(OVERVIEW).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withOverview(body);
            }
        });

        return this;
    }

    public SeriesElement withGenres() {
        this.wrappedElement.getChild(GENRES).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String genres = Strings.normalizePipeSeparated(body);
                SeriesElement.this.seriesBuilder.withGenres(genres);
            }
        });

        return this;
    }

    public SeriesElement withActors() {
        this.wrappedElement.getChild(ACTORS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String actors = Strings.normalizePipeSeparated(body);
                SeriesElement.this.seriesBuilder.withActors(actors);
            }
        });

        return this;
    }

    public SeriesElement withPoster(final StreamFactory streamFactory) {
        Validate.isNonNull(streamFactory, "streamFactory");

        this.wrappedElement.getChild(POSTER).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                Bitmap bitmap = scaledBitmapFrom(body, streamFactory);
                SeriesElement.this.seriesBuilder.withPoster(bitmap);
            }
        });

        return this;
    }

    public SeriesElement withHandledContentOf(final EpisodeElement episodeElement) {
        Validate.isNonNull(episodeElement, "episodeElement");

        episodeElement.wrappedElement().setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                SeriesElement.this.seriesBuilder.withEpisode(episodeElement.handledContent());
            }
        });

        return this;
    }

    //Handled content---------------------------------------------------------------------------------------------------

    public Series handledContent() {
        return this.seriesBuilder.build();
    }

    public SeriesElement addingHandledContentTo(final Collection<Series> seriesCollection) {
        this.wrappedElement.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                seriesCollection.add(SeriesElement.this.handledContent());
            }
        });

        return this;
    }

    //Auxiliary---------------------------------------------------------------------------------------------------------

    private static Bitmap scaledBitmapFrom(String fileName, StreamFactory streamFactory) {
        if (Strings.isBlank(fileName)) return null;
        InputStream stream = streamFactory.streamForSeriesPoster(fileName);
        return BitmapFactory.decodeStream(stream);
    }
}
