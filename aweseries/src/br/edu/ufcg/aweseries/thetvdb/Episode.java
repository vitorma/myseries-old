package br.edu.ufcg.aweseries.thetvdb;

public class Episode {
    private boolean viewed;

    public boolean isViewed() {
        return this.viewed;
    }

    public void markAsViewed() {
        this.viewed = true;
    }

    public void markAsNotViewed() {
        this.viewed = false;        
    }

}
