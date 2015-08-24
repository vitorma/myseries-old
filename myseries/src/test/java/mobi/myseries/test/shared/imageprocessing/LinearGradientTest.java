package mobi.myseries.test.shared.imageprocessing;

import mobi.myseries.shared.imageprocessing.LinearGradient;

import org.junit.Assert;
import org.junit.Test;

public class LinearGradientTest {

    final static int ALPHA_MASK = 0xFF000000;
    final static int RED_MASK = 0x00FF0000;
    final static int GREEN_MASK = 0x0000FF00;
    final static int BLUE_MASK = 0x000000FF;
    final static int WHITE = 0xFFFFFFFF;
    final static int BLACK = 0xFF000000;
    final static int RED = 0xFFFF0000;
    final static int BLUE = 0xFF0000FF;

    @Test
    public final void testFirstPieceMustBeEqualToStartColor() {
        LinearGradient gradient =
                new LinearGradient().from(LinearGradientTest.BLACK).to(LinearGradientTest.WHITE)
                        .withSteps(7);
        Assert.assertEquals(LinearGradientTest.BLACK, gradient.colorOfPiece(0));

        gradient =
                new LinearGradient().from(LinearGradientTest.BLUE).to(LinearGradientTest.RED)
                        .withSteps(12);
        Assert.assertEquals(LinearGradientTest.BLUE, gradient.colorOfPiece(0));
    }

    @Test
    public final void testLastPieceMustBeEqualToEndColor() {
        LinearGradient gradient =
                new LinearGradient().from(LinearGradientTest.BLACK).to(LinearGradientTest.WHITE)
                        .withSteps(7);
        Assert.assertEquals(LinearGradientTest.WHITE, gradient.colorOfPiece(6));

        gradient =
                new LinearGradient().from(LinearGradientTest.BLUE).to(LinearGradientTest.RED)
                        .withSteps(12);
        Assert.assertEquals(LinearGradientTest.RED, gradient.colorOfPiece(11));
    }

    @Test
    public final void testMiddlePieceShouldBeEqualToAverageOfColorsWith3StepsFromBlackToWhite() {
        final int STEPS = 3;
        LinearGradient gradient =
                new LinearGradient().from(LinearGradientTest.BLACK).to(LinearGradientTest.WHITE)
                        .withSteps(STEPS);

        int startColorRed = (LinearGradientTest.BLACK & LinearGradientTest.RED_MASK) >> 16;
        int startColorGreen = (LinearGradientTest.BLACK & LinearGradientTest.GREEN_MASK) >> 8;
        int startColorBlue = (LinearGradientTest.BLACK & LinearGradientTest.BLUE_MASK);
        int endColorRed = (LinearGradientTest.WHITE & LinearGradientTest.RED_MASK) >> 16;
        int endColorGreen = (LinearGradientTest.WHITE & LinearGradientTest.GREEN_MASK) >> 8;
        int endColorBlue = (LinearGradientTest.WHITE & LinearGradientTest.BLUE_MASK);

        Assert.assertEquals((endColorRed + startColorRed) / 2,
                (LinearGradientTest.RED_MASK & gradient.colorOfPiece(1)) >> 16);
        Assert.assertEquals((startColorGreen + endColorGreen) / 2,
                (LinearGradientTest.GREEN_MASK & gradient.colorOfPiece(1))>> 8);
        Assert.assertEquals((startColorBlue + endColorBlue) / 2,
                LinearGradientTest.BLUE_MASK & gradient.colorOfPiece(1));
    }

    @Test
    public final void
            testPiecesShouldBeEqualToWeightedAverageOfColorsWith4StepsFromRedToBlue() {
        final int STEPS = 4;
        LinearGradient gradient =
                new LinearGradient().from(LinearGradientTest.RED).to(LinearGradientTest.BLUE)
                        .withSteps(STEPS);

        int startColorRed = (LinearGradientTest.RED & LinearGradientTest.RED_MASK) >> 16;
        int startColorGreen = (LinearGradientTest.RED & LinearGradientTest.GREEN_MASK) >> 8;
        int startColorBlue = (LinearGradientTest.RED & LinearGradientTest.BLUE_MASK);
        int endColorRed = (LinearGradientTest.BLUE & LinearGradientTest.RED_MASK) >> 16;
        int endColorGreen = (LinearGradientTest.BLUE & LinearGradientTest.GREEN_MASK) >> 8;
        int endColorBlue = (LinearGradientTest.BLUE & LinearGradientTest.BLUE_MASK);

        Assert.assertEquals(((2 * startColorRed) + (1 * endColorRed)) / 3,
                (LinearGradientTest.RED_MASK & gradient.colorOfPiece(1)) >> 16);
        Assert.assertEquals(((2 * startColorGreen) + (1 * endColorGreen)) / 3,
                (LinearGradientTest.GREEN_MASK & gradient.colorOfPiece(1)) >> 8);
        Assert.assertEquals(((2 * startColorBlue) + (1 * endColorBlue)) / 3,
                LinearGradientTest.BLUE_MASK & gradient.colorOfPiece(1));

        Assert.assertEquals(((1 * startColorRed) + (2 * endColorRed)) / 3,
                (LinearGradientTest.RED_MASK & gradient.colorOfPiece(2)) >> 16);
        Assert.assertEquals(((1 * startColorGreen) + (2 * endColorGreen)) / 3,
                (LinearGradientTest.GREEN_MASK & gradient.colorOfPiece(2)) >> 8);
        Assert.assertEquals(((1 * startColorBlue) + (2 * endColorBlue)) / 3,
                LinearGradientTest.BLUE_MASK & gradient.colorOfPiece(2));
    }
}
