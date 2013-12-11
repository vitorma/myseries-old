package mobi.myseries.application.features.product;

import mobi.myseries.shared.Validate;

public class Availability {

    public static final Availability NotAvailable = new Availability(Price.NotAvailable, false);

    private final boolean isOwned;
    private final Price price;

    public Availability(Price price, boolean isOwned) {
        Validate.isNonNull(price, "price");

        this.price = price;
        this.isOwned = isOwned;
    }

    public boolean isOwned() {
        return this.isOwned;
    }

    public Price price() {
        return this.price;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isOwned ? 1231 : 1237);
        result = prime * result + ((price == null) ? 0 : price.hashCode());
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
        Availability other = (Availability) obj;
        if (isOwned != other.isOwned)
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        return true;
    }
}