package mobi.myseries.application.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.Dates;

public class ScheduleDays {
    private TreeMap<Date, Set<Episode>> days;

    public ScheduleDays(int sortMode) {
        this.days = new TreeMap<Date, Set<Episode>>(comparator(sortMode));
    }

    public void add(Episode episode) {
        Date day = episode.airDate();

        this.get(day).add(episode);
    }

    public void addAll(Collection<Episode> episodes) {
        for (Episode e : episodes) {
            this.add(e);
        }
    }

    private Set<Episode> get(Date day) {
        if (!this.days.containsKey(day)) {
            this.days.put(day, new HashSet<Episode>());
        }

        return this.days.get(day);
    }

    public List<Date> getDays() {
        return new ArrayList<Date>(this.days.keySet());
    }

    public List<Episode> getEpisodes(Date day) {
        return new ArrayList<Episode>(this.get(day));
    }

    public List<Episode> getEpisodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Set<Episode> set : this.days.values()) {
            episodes.addAll(set);
        }

        return episodes;
    }

    public int numberOfDays() {
        return this.days.size();
    }

    public int numberOfEpisodes() {
        int n = 0;

        for (Date day : this.days.keySet()) {
            n += this.days.get(day).size();
        }

        return n;
    }

    public int numberOfEpisodes(Date day) {
        return this.days.get(day).size();
    }

    public List<Object> toList() {
        List<Object> list = new ArrayList<Object>();

        for (Date day : this.days.keySet()) {
            list.add(day);
            list.addAll(this.days.get(day));
        }

        return list;
    }

    //------------------------------------------------------------------------------------------------------------------

    private static Comparator<Date> comparator(int sortMode) {
        switch(sortMode) {
            case SortMode.NEWEST_FIRST:
                return new Comparator<Date>() {
                    @Override
                    public int compare(Date d1, Date d2) {
                        return Dates.compareByNullLast(d2, d1);
                    }
                    
                };
            case SortMode.OLDEST_FIRST:
                return new Comparator<Date>() {
                    @Override
                    public int compare(Date d1, Date d2) {
                        return Dates.compareByNullLast(d1, d2);
                    }
                    
                };
            default:
                return null;
        }
    }
}
