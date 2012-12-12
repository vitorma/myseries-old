package mobi.myseries.shared.imageprocessing;

import org.junit.Assert;
import org.junit.Test;

public class ColorTest {

    @Test
    public final void testConversionConsistency() {

        final double delta = 1;
        Color color = Color.fromRgb(50, 100, 150);
        Color color2 = Color.fromLms(color.l(), color.m(), color.s());

        Assert.assertEquals(color.l(), color2.l(), delta);
        Assert.assertEquals(color.m(), color2.m(), delta);
        Assert.assertEquals(color.s(), color2.s(), delta);
        Assert.assertEquals(color.r(), color2.r(), delta);
        Assert.assertEquals(color.g(), color2.g(), delta);
        Assert.assertEquals(color.b(), color2.b(), delta);
    }
}
