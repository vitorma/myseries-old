package mobi.myseries.application.backup.json;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Validate;

public class SeriesSnippet {
    public static final int INVALID_SERIES_ID = -1;

    public static class Builder {
        private int id;

        private final Set<EpisodeSnippet> episodes;

        private Builder() {
            id = Invalid.SERIES_ID;
            episodes = new HashSet<EpisodeSnippet>();
        }

        public SeriesSnippet build() {
            final SeriesSnippet series = new SeriesSnippet(id);

            return series.includingAll(episodes);
        }

        public Builder withTvdbId(int id) {
            this.id = id;
            return this;
        }

    }

    public static SeriesSnippet.Builder builder() {
        return new SeriesSnippet.Builder();
    }

    private final int mId;
    private HashSet<EpisodeSnippet> mEpisodes;

    private SeriesSnippet(int id) {
        Validate.isTrue(id >= 0, "id should be non-negative");

        this.mId = id;
        mEpisodes = new HashSet<EpisodeSnippet>();
    }

    public Set<EpisodeSnippet> episodes() {
        return mEpisodes;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SeriesSnippet)
                && (((SeriesSnippet) obj).mId == mId);
    }

    @Override
    public int hashCode() {
        return mId * 197;
    }

    public int id() {
        return mId;
    }

    public SeriesSnippet includingAll(Collection<EpisodeSnippet> episodes) {
        Validate.isNonNull(episodes, "items");
        mEpisodes.addAll(episodes);

        return this;
    }
}
