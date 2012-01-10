package br.edu.ufcg.aweseries.model;

public interface EpisodeListener {

    public void onMarkedAsSeen(Episode e);

    public void onMarkedAsNotSeen(Episode e);

    public void onMerged(Episode e);
}
