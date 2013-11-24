package mobi.myseries.application.features;

/**
 * The product entity. It should be implemented by each store and derived classes
 * in general should not override equals and hashCode.
 */
public abstract class Product<I extends ProductId> {

    public abstract I id();
    public abstract String name();
    public abstract String price();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id() == null) ? 0 : this.id().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product<?> other = (Product<?>) obj;
        if (this.id() == null) {
            if (other.id() != null)
                return false;
        } else if (!this.id().equals(other.id()))
            return false;
        return true;
    }
}
