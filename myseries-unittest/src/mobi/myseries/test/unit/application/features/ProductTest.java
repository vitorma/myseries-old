package mobi.myseries.test.unit.application.features;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import android.test.AndroidTestCase;

import mobi.myseries.application.features.product.Availability;
import mobi.myseries.application.features.product.Price;
import mobi.myseries.application.features.product.Product;
import mobi.myseries.application.features.product.ProductDescription;
import mobi.myseries.application.features.product.Sku;

public class ProductTest extends AndroidTestCase {

    private static final Sku sku1 = new Sku("PRODUCT1");
    private static final Sku sku2 = new Sku("PRODUCT2");
    private static final ProductDescription description1 = newProductDescriptionWithSku(sku1);
    private static final ProductDescription description2 = newProductDescriptionWithSku(sku2);

    private static final Price price1 = new Price("$1.00");
    private static final Price price2 = new Price("$2.00");
    private static final Availability availability1 = new Availability(price1, true);
    private static final Availability availability2 = new Availability(price2, false);

    private static ProductDescription newProductDescriptionWithSku(Sku sku) {
        return new ProductDescription(sku);
    }

    /* Validate constructor */

    public void testItCannotCreateAProductWithNullAvailability() {
        try {
            new Product(description1, null);
            fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testItCannotCreateAProductWithNullDescription() {
        try {
            new Product(null, availability1);
            fail("It should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    /* Sku */

    public void testTheSkuOfTheProductIsTheSameAsTheSkuOfItsDescription() {
        ProductDescription productDescription = newProductDescriptionWithSku(sku1);
        Product product = new Product(productDescription, availability1);

        assertThat(product.sku(), equalTo(product.description().sku()));
    }

    /* ProductDescription */

    public void testTheDescriptionOfTheProductIsTheDescriptionUsedInItsConstruction() {
        ProductDescription description = description1;
        Product product = new Product(description, availability1);

        assertThat(product.description(), equalTo(description));
    }

    /* Price */

    public void testThePriceOfTheProductIsTheSameAsThePriceOfItsAvailability() {
        Availability availability = availability1;
        Product product = new Product(description1, availability);

        assertThat(product.price(), equalTo(availability.price()));
    }

    /* Ownership */

    public void testTheOwnershipOfTheProductIsTheSameAsTheOwnershipOfItsAvailability() {
        Availability availability = availability1;
        Product product = new Product(description1, availability);

        assertThat(product.isOwned(), equalTo(availability.isOwned()));
    }

    /* Comparison */

    private final static ProductDescription descriptionA = new ProductDescription(sku1) {
        @Override
        public String name() {
            return "A";
        }
    };
    private final static ProductDescription descriptionB = new ProductDescription(sku1) {
        @Override
        public String name() {
            return "B";
        }
    };
    private final static ProductDescription descriptionC = new ProductDescription(sku1) {
        @Override
        public String name() {
            return "C";
        }
    };
    private final static Availability availabilityOwned = new Availability(price1, true);
    private final static Availability availabilityNotOwned = new Availability(price1, false);

    private final static Product ownedA = new Product(descriptionA, availabilityOwned);
    private final static Product ownedB = new Product(descriptionB, availabilityOwned);
    private final static Product ownedC = new Product(descriptionC, availabilityOwned);
    private final static Product notOwnedA = new Product(descriptionA, availabilityNotOwned);
    private final static Product notOwnedB = new Product(descriptionB, availabilityNotOwned);
    private final static Product notOwnedC = new Product(descriptionC, availabilityNotOwned);

    public void testOwnedProductsAreOrderedByName() {
        // Transitiveness
        assertThat(ownedA, lessThan(ownedB));
        assertThat(ownedA, lessThan(ownedC));
        assertThat(ownedB, lessThan(ownedC));

        // Invertible
        assertThat(ownedB, greaterThan(ownedA));
        assertThat(ownedC, greaterThan(ownedA));
        assertThat(ownedC, greaterThan(ownedB));
    }

    public void testNotOwnedProductsAreOrderedByName() {
        // Transitiveness
        assertThat(notOwnedA, lessThan(notOwnedB));
        assertThat(notOwnedA, lessThan(notOwnedC));
        assertThat(notOwnedB, lessThan(notOwnedC));

        // Invertible
        assertThat(notOwnedB, greaterThan(notOwnedA));
        assertThat(notOwnedC, greaterThan(notOwnedA));
        assertThat(notOwnedC, greaterThan(notOwnedB));
    }

    public void testOwnedProdudctsAwaysComeAfterNotOwnedProducts() {
        assertThat(notOwnedA, lessThan(ownedA));
        assertThat(notOwnedA, lessThan(ownedB));
        assertThat(notOwnedA, lessThan(ownedC));

        assertThat(notOwnedB, lessThan(ownedA));
        assertThat(notOwnedB, lessThan(ownedB));
        assertThat(notOwnedB, lessThan(ownedC));

        assertThat(notOwnedC, lessThan(ownedA));
        assertThat(notOwnedC, lessThan(ownedB));
        assertThat(notOwnedC, lessThan(ownedC));
    }

    public void testHowTheOrderShouldBeWithTheTestProducts() {
        assertThat(notOwnedA, lessThan(notOwnedB));
        assertThat(notOwnedB, lessThan(notOwnedC));
        assertThat(notOwnedC, lessThan(ownedA));
        assertThat(ownedA,    lessThan(ownedB));
        assertThat(ownedB,    lessThan(ownedC));
    }

    public void testProductsWithTheSameNameAndOwnershipAreEquivalent() {
        assertThat(ownedA, not(lessThan(ownedA)));
        assertThat(ownedA, not(greaterThan(ownedA)));
    }

    /* Entity tests*/

    public void testTwoProductsAreEqualIfTheyHaveTheSameDescription() {
        ProductDescription description = description1;

        Product p1 = new Product(description, availability1);
        Product p2 = new Product(description, availability2);

        assertThat(p1, equalTo(p2));
        assertThat(p2, equalTo(p1));
    }

    public void testTwoProductsAreDifferentIfTheyHaveDifferentDescriptions() {
        Product p1 = new Product(description1, availability1);
        Product p2 = new Product(description2, availability1);

        assertThat(p1, not(equalTo(p2)));
        assertThat(p2, not(equalTo(p1)));
    }

    public void testNoProductIsEqualToNull() {
        Product product = new Product(description1, availability1);

        assertThat(product, not(equalTo(null)));
    }
}
