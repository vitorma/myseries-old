package mobi.myseries.test.unit.domain.source;

import java.util.Collection;
import java.util.LinkedList;

import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.CollectionFilter;

import org.junit.Assert;
import org.junit.Test;

public class CollectionFilterTest {

    @Test(expected = Exception.class)
    public final void testCreateFilterWithNullSpecification()
    {
        new CollectionFilter<Integer>(null);
    }

    @Test
    public final void testFilter() {
        CollectionFilter<Integer> pairNumbers =
                new CollectionFilter<Integer>(new AbstractSpecification<Integer>() {
                    @Override
                    public boolean isSatisfiedBy(Integer t) {
                        return (t % 2) == 0;
                    }
                });

        Collection<Integer> decimalDigits = new LinkedList<Integer>();

        for (int i = 0; i < 10; ++i) {
            decimalDigits.add(i);
        }

        Collection<Integer> pairs = pairNumbers.in(decimalDigits);

        for (int i = 0; i < 10; ++i) {
            if ((i % 2) == 0) {
                Assert.assertTrue(pairs.contains(i));
            }
            else {
                Assert.assertFalse(pairs.contains(i));
            }
        }
    }
}
