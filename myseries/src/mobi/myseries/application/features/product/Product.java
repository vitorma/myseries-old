package mobi.myseries.application.features.product;

import mobi.myseries.shared.Validate;

public class Product {

    private final ProductDescription description;
    private final Availability availability;

    public Product(ProductDescription description, Availability availability) {
        Validate.isNonNull(description, "description");
        Validate.isNonNull(availability, "availability");

        this.description = description;
        this.availability = availability;
    }

    public Sku sku() {
        return this.description.sku();
    }

    public ProductDescription description() {
        return this.description;
    }

    public Price price() {
        return this.availability.price();
    }

    public boolean isOwned() {
        return this.availability.isOwned();
    }

    // Equals and HashCode

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
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
        Product other = (Product) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }
}
