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

    private int tvdbId;
    private String title;
    private String overview;
    private String poster;

    public SearchResult() { }

    public SearchResult(Parcel in) {
        this.tvdbId = in.readInt();
        this.title = in.readString();
        this.overview = in.readString();
        this.poster = in.readString();
    }

    public SearchResult copy() {
        return new SearchResult()
            .setTvdbId(this.tvdbId)
            .setTitle(this.title)
            .setOverview(this.overview)
            .setPoster(this.poster);
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tvdbId);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.poster);
    }

    public int tvdbId() {
        return this.tvdbId;
    }

    public String title() {
        return this.title;
    }

    public String overview() {
        return this.overview;
    }

    public String poster() {
        return this.poster;
    }

    public SearchResult setTvdbId(int tvdbId) {
        this.tvdbId = tvdbId;
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

    public SearchResult setPoster(String poster) {
        this.poster = poster;
        return this;
    }

    @Override
    public int hashCode() {
        return this.tvdbId;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SearchResult && ((SearchResult) other).tvdbId == this.tvdbId;
    }
}
