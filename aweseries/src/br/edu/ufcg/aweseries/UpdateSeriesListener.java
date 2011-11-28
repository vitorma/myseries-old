package br.edu.ufcg.aweseries;

public interface UpdateSeriesListener {
    void onUpdateStarted();
    
    void onUpdateFailed();
    
    void onUpdateFinished();
}
