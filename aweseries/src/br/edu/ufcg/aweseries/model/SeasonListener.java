package br.edu.ufcg.aweseries.model;

public interface SeasonListener {

    public void onMarkAsSeen(Season season);

    public void onMarkAsNotSeen(Season season);

    public void onChangeNumberOfSeenEpisodes(Season season);

    public void onChangeNextEpisodeToSee(Season season);

    public void onMerge(Season season);
}
