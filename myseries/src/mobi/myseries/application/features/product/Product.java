package mobi.myseries.application.features.product;

import mobi.myseries.shared.Validate;

// TODO(Gabriel): Remove Comparable interface and extract the Comparator.
//                Do not forget the tests, they can be reused ;).
public class Product implements Comparable<Product> {

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

    @Override
    public int compareTo(Product that) {
        if (this.isOwned() == that.isOwned()) {
            return this.description().name().compareTo(that.description().name());
        } else {
            return (this.isOwned() ? 1 : -1);
        }
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
