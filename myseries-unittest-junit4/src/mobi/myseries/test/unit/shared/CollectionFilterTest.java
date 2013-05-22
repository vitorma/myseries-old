package mobi.myseries.test.unit.shared;

import java.util.Collection;
import java.util.LinkedList;

import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.CollectionFilter;
import mobi.myseries.shared.Specification;

import org.junit.Assert;
import org.junit.Test;

public class CollectionFilterTest {

    private static final Specification<Integer> EVEN_NUMBER_SPECIFICATION =
            new AbstractSpecification<Integer>() {
                @Override
                public boolean isSatisfiedBy(Integer t) {
                    return (t % 2) == 0;
                }
            };

    @Test(expected = IllegalArgumentException.class)
    public final void testCreateFilterWithNullSpecification() {
        new CollectionFilter<Integer>(null);
    }

    @Test
    public final void testFilter() {
        CollectionFilter<Integer> evenNumbers =
                new CollectionFilter<Integer>(EVEN_NUMBER_SPECIFICATION);

        Collection<Integer> decimalDigits = new LinkedList<Integer>();

        for (int i = 0; i < 10; ++i) {
            decimalDigits.add(i);
        }

        Collection<Integer> evens = evenNumbers.in(decimalDigits);

        for (int i = 0; i < 10; ++i) {
            if ((i % 2) == 0) {
                Assert.assertTrue(evens.contains(i));
            } else {
                Assert.assertFalse(evens.contains(i));
            }
        }
    }
}
