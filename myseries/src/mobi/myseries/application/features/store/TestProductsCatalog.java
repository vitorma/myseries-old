package mobi.myseries.application.features.store;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mobi.myseries.application.features.Feature;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;

public class TestProductsCatalog extends ProductCatalog {

    private static final Set<ProductDescription> implementedProducts =
            Collections.unmodifiableSet(new HashSet<ProductDescription>(Arrays.asList(
                    new ProductDescription(new Sku("android.test.purchased")){
                        @Override
                        public String name() {
                            return "Purchased product";
                        }
                        @Override
                        public String description() {
                            return "A product that can be purchased without any costs.\n\n" +
                                    "Nowadays it enables CLOUD_BACKUP.\n\n" +
                                    "If you have purchased this product, it will be back as not " +
                                    "purchased in some time.";
                        }
                        @Override
                        public Set<Feature> features() {
                            return new HashSet<Feature>(Arrays.asList(Feature.CLOUD_BACKUP));
                        }
                    },
                    new ProductDescription(new Sku("android.test.canceled")) {
                        @Override
                        public String name() {
                            return "Canceled product";
                        }
                        @Override
                        public String description() {
                            return "A product that Google Play treats as canceled.";
                        }
                    },
                    new ProductDescription(new Sku("android.test.refunded")) {
                        @Override
                        public String name() {
                            return "Refunded product";
                        }
                        @Override
                        public String description() {
                            return "A product that Google Play treats as refunded.";
                        }
                    },
                    new ProductDescription(new Sku("android.test.item_unavailable")) {
                        @Override
                        public String name() {
                            return "Unavailable product";
                        }
                        @Override
                        public String description() {
                            return "A product that Google Play treats as unavailable.";
                        }
                    },
                    new ProductDescription(new Sku("invalid_sku")) {
                        @Override
                        public String name() {
                            return "Invalid product";
                        }
                        @Override
                        public String description() {
                            return "Its price should be unavailable.";
                        }
                    })));

    @Override
    public Set<ProductDescription> implementedProducts() {
        return implementedProducts;
    }
}
