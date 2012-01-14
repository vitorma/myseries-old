package br.edu.ufcg.aweseries.test.unit.util;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.aweseries.util.Validate;

public class ValidateTest {

	@Test(expected=IllegalArgumentException.class)
	public void testIsTrueHavingAFalseAssertionThrowingIllegalArgumentException() {
		Validate.isTrue(false, "");
	}

	@Test
	public void testIsTrueHavingATrueAssertionThrowingIllegalArgumentException() {
		try {
			Validate.isTrue(true, "");
		} catch (IllegalArgumentException e) {
			Assert.fail();
		}
	}

	@Test(expected=RuntimeException.class)
	public void testIsTrueHavingAFalseAssertionThrowingRuntimeException() {
		Validate.isTrue(false, new RuntimeException());
	}

	@Test
	public void testIsTrueHavingATrueAssertionThrowingRuntimeException() {
		try {
			Validate.isTrue(true, new RuntimeException());
		} catch (RuntimeException e) {
			Assert.fail();
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsNonNullHavingANullObjectThrowingIllegalArgumentException() {
		Validate.isNonNull(null, "");
	}

	@Test
	public void testIsNonNullHavingANonNullObjectThrowingIllegalArgumentException() {
		try {
			Validate.isNonNull(new Object(), "");
		} catch (IllegalArgumentException e) {
			Assert.fail();
		}
	}

	@Test(expected=RuntimeException.class)
	public void testIsNonNullHavingANullObjectThrowingRuntimeException() {
		Validate.isNonNull(null, new RuntimeException());
	}

	@Test
	public void testIsNonNullHavingANonNullObjectThrowingRuntimeException() {
		try {
			Validate.isNonNull(new Object(), new RuntimeException());
		} catch (RuntimeException e) {
			Assert.fail();
		}
	}
}
