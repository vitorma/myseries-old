package mobi.myseries.domain.model;

public interface Publisher<L> {

    public boolean register(L listener);

    public boolean deregister(L listener);
}
