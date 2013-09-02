package mobi.myseries.domain.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableSeries implements Parcelable {

    public static final Parcelable.Creator<ParcelableSeries> CREATOR = new Parcelable.Creator<ParcelableSeries>() {
        @Override
        public ParcelableSeries createFromParcel(Parcel in) {
            return new ParcelableSeries(in);
        }

        @Override
        public ParcelableSeries[] newArray(int size) {
            return new ParcelableSeries[size];
        }
    };

    private String tvdbId;
    private String title;
    private String overview;
    private String poster;

    public ParcelableSeries() { }

    public ParcelableSeries(Parcel in) {
        this.tvdbId = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.poster = in.readString();
    }

    public ParcelableSeries copy() {
        return new ParcelableSeries()
            .setTvdbId(this.tvdbId)
            .setTitle(this.title)
            .setOverview(this.overview)
            .setPoster(this.poster);
    }

    public Series toSeries() {
        return Series.builder()
            .withId(Integer.valueOf(this.tvdbId))
            .withName(this.title)
            .withOverview(this.overview)
            .withPosterFileName(this.poster)
            .build();
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tvdbId);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.poster);
    }

    public String tvdbId() {
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

    public ParcelableSeries setTvdbId(String tvdbId2) {
        this.tvdbId = tvdbId2;
        return this;
    }

    public ParcelableSeries setTitle(String title) {
        this.title = title;
        return this;
    }

    public ParcelableSeries setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public ParcelableSeries setPoster(String poster) {
        this.poster = poster;
        return this;
    }

    @Override
    public int hashCode() {
        return this.tvdbId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ParcelableSeries && ((ParcelableSeries) other).tvdbId == this.tvdbId;
    }
}
