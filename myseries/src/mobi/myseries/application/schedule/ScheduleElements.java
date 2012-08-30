package mobi.myseries.application.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.HasDate;
import mobi.myseries.shared.SortedList;
import mobi.myseries.shared.Specification;

public class ScheduleElements {
    private static final Comparator<HasDate> DEFAULT_COMPARATOR = comparator(SortMode.OLDEST_FIRST);

    private SortedList<HasDate> elements;

    public ScheduleElements() {
        this.elements = new SortedList<HasDate>(DEFAULT_COMPARATOR);
    }

    public int size() {
        return this.elements.size();
    }

    public boolean contains(HasDate element) {
        return this.elements.contains(element);
    }

    public HasDate get(int index) {
        return this.elements.get(index);
    }

    public boolean isEpisode(int index) {
        return this.isEpisode(this.get(index));
    }

    public boolean isEpisode(HasDate element) {
        return element != null && element.getClass() == Episode.class;
    }

    public boolean add(Episode element) {
        Day day = new Day(element.airDate());

        return (this.contains(day) || this.elements.add(day)) &&
               (!this.contains(element) && this.elements.add(element));
    }

    public boolean remove(Episode element) {
        Day day = new Day(element.getDate());

        return (this.elements.remove(element)) &&
               (this.containsEpisodesOn(day) || this.elements.remove(day));
    }

    public void removeBy(Specification<Episode> specification) {
        this.removeAll(this.episodesBy(specification));
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        List<Episode> episodes = new LinkedList<Episode>();

        for (HasDate element : this.elements) {
            if (!this.isEpisode(element)) {continue;}

            Episode episode = (Episode) element;

            if (specification.isSatisfiedBy(episode)) {
                episodes.add(episode);
            }
        }

        return episodes;
    }

    public List<Episode> getEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (HasDate element : this.elements) {
            if (this.isEpisode(element)) {
                episodes.add((Episode) element);
            }
        }

        return episodes;
    }

    public void addAll(Collection<Episode> collection) {
        for (Episode e : collection) {
            this.add(e);
        }
    }

    public void removeAll(Collection<Episode> collection) {
        for (Episode e : collection) {
            this.remove(e);
        };
    }

    private boolean containsEpisodesOn(Day day) {
        int i = this.elements.indexOf(day) + 1;

        return i < this.size() && this.get(i).hasSameDateAs(day);
    }

    private static Comparator<HasDate> comparator(int sortMode) {
        switch(sortMode) {
            case SortMode.OLDEST_FIRST:
                return naturalComparator();
            case SortMode.NEWEST_FIRST:
                return reversedComparator();
            default:
                return null;
        }
    }

    private static Comparator<HasDate> naturalComparator() {
        return new Comparator<HasDate>() {
            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = left.getDate().compareTo(right.getDate());

                if (dateComparation != 0) {return dateComparation;}

                return typeComparator().compare(left, right);
            }
        };
    }

    private static Comparator<HasDate> reversedComparator() {
        return new Comparator<HasDate>() {
            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = right.getDate().compareTo(left.getDate());

                if (dateComparation != 0) {return dateComparation;}

                return typeComparator().compare(left, right);
            }
        };
    }

    private static Comparator<HasDate> typeComparator() {
        return new Comparator<HasDate>() {
            private static final int TYPE_DAY = 0;
            private static final int TYPE_EPISODE = 1;

            @Override
            public int compare(HasDate left, HasDate right) {
                return typeOf(left) - typeOf(right);
            }

            private int typeOf(HasDate object) {
                return object.getClass() == Day.class ? TYPE_DAY : TYPE_EPISODE;
            }
        };
    }
}