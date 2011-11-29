package br.edu.ufcg.aweseries;

public interface UpdateListener {
    void onUpdateStart();
    
    void onUpdateFailure();
    
    void onUpdateSuccess();
}
