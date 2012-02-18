package br.edu.ufcg.aweseries.test.unit.util;

import java.util.IllegalFormatConversionException;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.aweseries.util.Validate;

public class ValidateTest {

    /* isTrue */

    private static final String NON_BLANK_STRING = "I'm non-blank.";
    private static final String SHOULD_BE_NON_BLANK = "%s should be non-blank";
    private static final String SHOULD_BE_NON_NULL = "%s should be non-null";

    @Test(expected = IllegalArgumentException.class)
    public void testIsTrueHavingAFalseAssertionThrowingIllegalArgumentException() {
        Validate.isTrue(false, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsTrueHavingAFalseAssertionThrowingIllegalArgumentExceptionWithNullMessage() {
        String message = null;
        Validate.isTrue(false, message);
    }

    @Test(expected = IllegalArgumentException.class)
    public
            void
            testIsTrueHavingAFalseAssertionThrowingIllegalArgumentExceptionWithNullMessageWithArguments() {
        String message = null;
        Validate.isTrue(false, message, 10, 200, 0.0);
    }

    @Test
    public void testIsTrueHavingAFalseAssertionThrowingIllegalArgumentExceptionWithGoodFormat() {
        String message = "Some message plus one %d";
        int value = 10;

        try {
            Validate.isTrue(false, message, value);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(message, value), e.getMessage());
        }
    }

    @Test(expected = IllegalFormatConversionException.class)
    public void testIsTrueHavingAFalseAssertionThrowingIllegalArgumentExceptionWithBadFormat() {
        String message = "Some message plus one %d";
        String badValue = "a";
        Validate.isTrue(false, message, badValue);
    }

    @Test
    public void testIsTrueHavingATrueAssertionThrowingIllegalArgumentException() {
        try {
            Validate.isTrue(true, "");
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }
    }

    @Test(expected = RuntimeException.class)
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

    /* isNonNull */

    @Test(expected = IllegalArgumentException.class)
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

    @Test
    public void testIsNonNullHavingAFalseAssertionThrowingIllegalArgumentExceptionWithGoodFormat() {
        String message = "Some message plus one %d";
        int value = 10;

        try {
            Validate.isNonNull(null, message, value);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(message, value), e.getMessage());
        }
    }

    @Test(expected = IllegalFormatConversionException.class)
    public void testIsNonNullHavingAFalseAssertionThrowingIllegalArgumentExceptionWithBadFormat() {
        String message = "Some message plus one %d";
        String badValue = "a";

        Validate.isNonNull(null, message, badValue);
    }

    @Test(expected = RuntimeException.class)
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

    @Test
    public void testIsNonNullHavingANullObjectThrowingIllegalArgumentExceptionWithAlias() {
        String alias = "Mr. Object";
        String shouldBeNonNull = SHOULD_BE_NON_NULL;

        try {
            Validate.isNonNull(null, alias);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(shouldBeNonNull, alias), e.getMessage());
        }
    }

    @Test
    public void testIsNonNullHavingANullObjectThrowingIllegalArgumentExceptionWithNullAlias() {
        String alias = null;
        String shouldBeNonNull = SHOULD_BE_NON_NULL;

        try {
            Validate.isNonNull(null, alias);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(shouldBeNonNull, alias), e.getMessage());
        }
    }

    @Test
    public void testIsNonNullHavingANullObjectThrowingIllegalArgumentExceptionWithBlankAlias() {
        String alias = "";
        String shouldBeNonNull = SHOULD_BE_NON_NULL;

        try {
            Validate.isNonNull(null, alias);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(shouldBeNonNull, alias), e.getMessage());
        }
    }

    /* isNonBlank */

    @Test(expected = RuntimeException.class)
    public void testIsNonBlankHavingANullStringThrowingRuntimeException() {
        Validate.isNonBlank(null, new RuntimeException());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNonBlankHavingAnEmptyStringThrowingRuntimeException() {
        Validate.isNonBlank("", new RuntimeException());
    }

    @Test(expected = RuntimeException.class)
    public void testIsNonBlankHavingABlankStringThrowingRuntimeException() {
        Validate.isNonBlank("  \0   \t \0    ", new RuntimeException());
    }

    @Test
    public void testIsNonBlankHavingANonBlankStringThrowingRuntimeException() {
        try {
            Validate.isNonBlank(NON_BLANK_STRING, new RuntimeException());
        } catch (RuntimeException e) {
            Assert.fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNonBlankHavingANullStringThrowingIllegalArgumentExceptionWithGoodFormat() {
        Validate.isNonBlank(null, "Some%s", "thing");
    }

    @Test(expected = IllegalFormatConversionException.class)
    public void testIsNonBlankHavingANullStringThrowingIllegalArgumentExceptionWithBadFormat() {
        Validate.isNonBlank(null, "Some%d", "thing");
    }

    @Test()
    public void testIsNonBlankHavingANonBlankStringThrowingIllegalArgumentException() {
        try {
            Validate.isNonBlank(NON_BLANK_STRING, "Some%s", "thing");
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }
    }

    @Test
    public void testIsNonBlankHavingANullStringThrowingIllegalArgumentExceptionWithAlias() {
        String shouldBeNonBlank = SHOULD_BE_NON_BLANK;
        String alias = "Mrs. String";

        try {
            Validate.isNonBlank(NON_BLANK_STRING, alias);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(shouldBeNonBlank, alias), e.getMessage());
        }
    }

    @Test
    public void testIsNonBlankHavingAnEmptyStringThrowingIllegalArgumentExceptionWithBlankAlias() {
        String shouldBeNonBlank = SHOULD_BE_NON_BLANK;
        String alias = "";

        try {
            Validate.isNonBlank("", alias);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(shouldBeNonBlank, alias), e.getMessage());
        }
    }

    @Test
    public void testIsNonBlankHavingABlankStringThrowingIllegalArgumentExceptionWithNullAlias() {
        String shouldBeNonBlank = SHOULD_BE_NON_BLANK;
        String alias = null;

        try {
            Validate.isNonBlank("", alias);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(String.format(shouldBeNonBlank, alias), e.getMessage());
        }
    }

    @Test
    public void testIsNonBlankHavingANonBlankStringThrowingIllegalArgumentExceptionWithAlias() {
        try {
            Validate.isNonBlank(NON_BLANK_STRING, "");
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }
    }
}
