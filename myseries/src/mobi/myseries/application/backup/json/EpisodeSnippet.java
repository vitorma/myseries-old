package mobi.myseries.application.backup.json;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.Validate;


public class EpisodeSnippet {
    private long mId;

    private EpisodeSnippet(long id) {
        Validate.isTrue(id >= 0, "id should be non-negative");

        mId = id;
    }

    public static EpisodeSnippet.Builder builder() {
        return new EpisodeSnippet.Builder();
    }

    public long id() {
        return mId;
    }

    public boolean isTheSameAs(Episode that) {
        return that != null
                && mId == that.id();
    }

    public boolean isTheSameAs(EpisodeSnippet that) {
        return that != null
                && mId == that.mId;
    }

    @Override
    public int hashCode() {
        return (int) mId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if(obj.getClass() == EpisodeSnippet.class) {
            EpisodeSnippet that = (EpisodeSnippet) obj;
            return isTheSameAs(that);
            
        }

        if(obj.getClass() == Episode.class) {
            Episode that = (Episode) obj;
            return isTheSameAs(that);
        }

        return false;
    }

    public static class Builder {
        private long mId;

        private Builder() {
            mId = Invalid.EPISODE_ID;
        }

        public Builder withId(long id) {
            mId = id;
            return this;
        }

        public EpisodeSnippet build() {
            EpisodeSnippet episode = new EpisodeSnippet(mId);

            return episode;
        }
    }
}
