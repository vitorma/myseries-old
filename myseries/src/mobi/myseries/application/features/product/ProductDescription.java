package mobi.myseries.application.features.product;

import java.util.HashSet;
import java.util.Set;

import mobi.myseries.application.features.Feature;
import mobi.myseries.shared.Validate;

/**
 * The description of a product, without price or ownership information.
 */
public class ProductDescription {

    private Sku sku;

    public ProductDescription(Sku sku) {
        Validate.isNonNull(sku, "sku");
        this.sku = sku;
    }

    public Sku sku() {
        return this.sku;
    }

    public String name() {
        return "ProductDescription(" + this.sku() + ")";
    }

    public String description() {
        return "The description of ProductDescription(" + this.sku() + ")";
    }

    public Set<Feature> features() {
        return new HashSet<Feature>();
    }

    @Override
    public String toString() {
        return "ProductDescription(" + this.sku() + ")";
    }

    // Equals and HashCode

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.sku() == null) ? 0 : this.sku().hashCode());
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
        ProductDescription other = (ProductDescription) obj;
        if (this.sku() == null) {
            if (other.sku() != null)
                return false;
        } else if (!this.sku().equals(other.sku()))
            return false;
        return true;
    }
}
