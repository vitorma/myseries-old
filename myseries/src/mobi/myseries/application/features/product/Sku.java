package mobi.myseries.application.features.product;

import java.io.Serializable;

import mobi.myseries.shared.Validate;

public class Sku implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String value;

    public Sku(String value) {
        Validate.isNonNull(value, "value");
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Sku other = (Sku) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Sku(" + value + ")";
    }
}
