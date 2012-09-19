package mobi.myseries.application;

import mobi.myseries.shared.ListenerSet;

public class ErrorService {

    private ListenerSet<ErrorServiceListener> listeners;

    public ErrorService() {
       this.listeners = new ListenerSet<ErrorServiceListener>();
    }
    
    public boolean registerListener(ErrorServiceListener l){
        return this.listeners.register(l);
    }

    public boolean deregisterListener(ErrorServiceListener l){
        return this.listeners.deregister(l);
    }

    public void notifyError(Exception e){
        for (ErrorServiceListener l : listeners) {
            l.onError(e);
        }
    }
}
