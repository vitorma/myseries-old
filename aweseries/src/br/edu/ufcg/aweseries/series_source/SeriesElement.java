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
    public static final String ID = "id";
    public static final String NAME = "SeriesName";
    public static final String STATUS = "Status";
    public static final String AIR_DAY = "Airs_DayOfWeek";
    public static final String AIR_TIME = "Airs_Time";
    public static final String AIRDATE = "FirstAired";
    public static final String RUNTIME = "Runtime";
    public static final String NETWORK = "Network";
    public static final String OVERVIEW = "Overview";
    public static final String GENRES = "Genre";
    public static final String ACTORS = "Actors";
    public static final String POSTER = "poster";

    private static final int INVALID_SERIES_ID = -1;

    private Element element;
    private Series.Builder seriesBuilder;

    //Construction------------------------------------------------------------------------------------------------------

    private SeriesElement(RootElement root) {
        Validate.isNonNull(root, "root");

        this.element = root.requireChild("Series");
        this.seriesBuilder = new Series.Builder();
    }

    //Factory-----------------------------------------------------------------------------------------------------------

    public static SeriesElement from(RootElement root) {
        return new SeriesElement(root);
    }

    //Content handling--------------------------------------------------------------------------------------------------

    public SeriesElement withId() {
        this.element.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body, INVALID_SERIES_ID);
                SeriesElement.this.seriesBuilder.withId(id);
            }
        });

        return this;
    }

    public SeriesElement withName() {
        this.element.getChild(NAME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withName(body);
            }
        });

        return this;
    }

    public SeriesElement withStatus() {
        this.element.getChild(STATUS).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withStatus(body);
            }
        });

        return this;
    }

    public SeriesElement withAirDay() {
        this.element.getChild(AIR_DAY).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withAirsDay(body);
            }
        });

        return this;
    }

    public SeriesElement withAirTime() {
        this.element.getChild(AIR_TIME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withAirsTime(body);
            }
        });

        return this;
    }

    public SeriesElement withAirdate() {
        this.element.getChild(AIRDATE).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withFirstAired(body);
            }
        });

        return this;
    }

    public SeriesElement withRuntime() {
        this.element.getChild(RUNTIME).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withRuntime(body);
            }
        });

        return this;
    }

    public SeriesElement withNetwork() {
        this.element.getChild(NETWORK).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withNetwork(body);
            }
        });

        return this;
    }

    public SeriesElement withOverview() {
        this.element.getChild(OVERVIEW).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                SeriesElement.this.seriesBuilder.withOverview(body);
            }
        });

        return this;
    }

    public SeriesElement withGenres() {
        this.element.getChild(GENRES).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                String genres = Strings.normalizePipeSeparated(body);
                SeriesElement.this.seriesBuilder.withGenres(genres);
            }
        });

        return this;
    }

    public SeriesElement withActors() {
        this.element.getChild(ACTORS).setEndTextElementListener(new EndTextElementListener() {
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

        this.element.getChild(POSTER).setEndTextElementListener(new EndTextElementListener() {
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
        this.element.setEndElementListener(new EndElementListener() {
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
