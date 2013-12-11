package mobi.myseries.application.features.product;

import mobi.myseries.shared.Validate;

public class Product {

    private final Price price;
    private final ProductDescription description;

    public Product(Price price, ProductDescription description) {
        Validate.isNonNull(price, "price");
        Validate.isNonNull(description, "description");

        this.price = price;
        this.description = description;
    }

    public Sku sku() {
        return this.description.sku();
    }

    public ProductDescription description() {
        return this.description;
    }

    public Price price() {
        return this.price;
    }

    // TODO(Gabriel): public boolean isOwned()

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
