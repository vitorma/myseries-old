package mobi.myseries.application.features.googleplay;

import mobi.myseries.application.features.ProductId;
import mobi.myseries.shared.Validate;

public class GooglePlayProductId implements ProductId {

    private final String value;

    public GooglePlayProductId(String value) {
        Validate.isNonNull(value, "value");
        this.value = value;
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
        GooglePlayProductId other = (GooglePlayProductId) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GooglePlayProductId(" + value + ")";
    }
}
