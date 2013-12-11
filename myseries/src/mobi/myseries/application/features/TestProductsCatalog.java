package mobi.myseries.application.features;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;

public class TestProductsCatalog extends ProductCatalog {

    private static final Set<ProductDescription> implementedProducts =
            Collections.unmodifiableSet(new HashSet<ProductDescription>(Arrays.asList(
                    new ProductDescription(new Sku("android.test.purchased")),
                    new ProductDescription(new Sku("android.test.canceled")),
                    new ProductDescription(new Sku("android.test.refunded")),
                    new ProductDescription(new Sku("android.test.item_unavailable")))));

    @Override
    public Set<ProductDescription> implementedProducts() {
        return implementedProducts;
    }
}
