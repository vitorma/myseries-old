package mobi.myseries.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable {

    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    private String traktId = "";
    private String title = "";
    private String overview = "";
    private String genres = "";
    private String poster = "";

    public SearchResult() { }

    public SearchResult(Parcel in) {
        this.traktId = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.genres = in.readString();
        this.poster = in.readString();
    }

    public SearchResult copy() {
        return new SearchResult()
            .setTraktId(this.traktId)
            .setTitle(this.title)
            .setOverview(this.overview)
            .setGenres(this.genres)
            .setPoster(this.poster);
    }

    public Series toSeries() {
        return Series.builder()
            .withTraktId(Integer.valueOf(this.traktId))
            .withTitle(this.title)
            .withOverview(this.overview)
            .withGenres(this.genres)
            .withPoster(this.poster)
            .build();
    }

    public int traktIdAsInt() {
        return Integer.valueOf(this.traktId);
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.traktId);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.poster);
    }

    public String traktId() {
        return this.traktId;
    }

    public String title() {
        return this.title;
    }

    public String overview() {
        return this.overview;
    }

    public String genres() {
        return this.genres;
    }

    public String poster() {
        return this.poster;
    }

    public SearchResult setTraktId(String traktId) {
        this.traktId = traktId;
        return this;
    }

    public SearchResult setTitle(String title) {
        this.title = title;
        return this;
    }

    public SearchResult setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public SearchResult setGenres(String genres) {
        this.genres = genres;
        return this;
    }

    public SearchResult setPoster(String poster) {
        this.poster = poster;
        return this;
    }

    @Override
    public int hashCode() {
        return this.traktId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SearchResult &&
                ((SearchResult) other).traktId.equals(this.traktId);
    }
}
