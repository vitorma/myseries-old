package br.edu.ufcg.aweseries.model;

public interface DomainEntityListener<T> {
    
    void onUpdate(T entity);
}
