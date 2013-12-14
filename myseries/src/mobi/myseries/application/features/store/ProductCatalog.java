package mobi.myseries.application.features.store;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;

public abstract class ProductCatalog {

    public abstract Set<ProductDescription> implementedProducts();

    public Set<Product> productsWithoutPrice() {
        Set<Product> products = new HashSet<Product>();

        for (ProductDescription p : this.implementedProducts()) {
            products.add(new Product(p, Availability.NotAvailable));
        }

        return Collections.unmodifiableSet(products);
    }

    public Set<Sku> implementedProductsSkus() {
        Set<Sku> implementedSkus = new HashSet<Sku>();

        for (ProductDescription p : this.implementedProducts()) {
            implementedSkus.add(p.sku());
        }

        return Collections.unmodifiableSet(implementedSkus);
    }
}
