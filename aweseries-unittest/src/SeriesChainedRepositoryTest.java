import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.repository.SeriesChainedRepository;

public class SeriesChainedRepositoryTest {
	private SeriesChainedRepository scr1;
	private SeriesChainedRepository scr2;

	private SeriesChainedRepository newSeriesChainedRepository() {
		SeriesChainedRepository scr = new SeriesChainedRepository() {
			@Override
			public void insert(Series series) {}

			@Override
			public void update(Series series) {}

			@Override
			public void delete(Series series) {}

			@Override
			public void clear() {}

			@Override
			public Collection<Series> getAll() {return null;}

			@Override
			public Series get(String seriesId) {return null;}

			@Override
			public boolean contains(String seriesId) {return false;}
		};

		return scr;
	}

	@Before
	public void setUp() {
		scr1 = newSeriesChainedRepository();
		scr2 = newSeriesChainedRepository();
	}

	@Test
	public void testNextRepository() {
		Assert.assertNull(this.scr1.nextRepository());
		Assert.assertNull(this.scr2.nextRepository());

		this.scr1.chainResponsibilityTo(this.scr2);
		Assert.assertEquals(this.scr2, this.scr1.nextRepository());
		Assert.assertNull(this.scr2.nextRepository());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testChainResponsibilityToNullRepository() {
		this.scr1.chainResponsibilityTo(null);
	}

	@Test
	public void testChainResponsibilityTo() {
		Assert.assertNull(this.scr1.nextRepository());
		Assert.assertNull(this.scr2.nextRepository());

		for (int i=1; i<=10; i++) {
			SeriesChainedRepository scr = newSeriesChainedRepository();
			this.scr1.chainResponsibilityTo(this.scr2);
			this.scr2.chainResponsibilityTo(scr);
			Assert.assertEquals(this.scr2, this.scr1.nextRepository());
			Assert.assertEquals(scr, this.scr2.nextRepository());
		}
	}
}
