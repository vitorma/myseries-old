package mobi.myseries.application.features;

import java.util.HashSet;
import java.util.Set;

import mobi.myseries.shared.Validate;

/**
 * A description of a product. It should be embedded in a Product in
 * order to receive its price.
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
        // TODO Auto-generated method stub
        //return null;
        return "GooglePlayProduct(" + this.sku() + ")";
    }

    public Set<Feature> features() {
        // TODO Auto-generated method stub
        //return null;
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
