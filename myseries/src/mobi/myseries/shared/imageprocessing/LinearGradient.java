package mobi.myseries.shared.imageprocessing;

import mobi.myseries.shared.Validate;

public class LinearGradient {
    private static final int RED_MASK = 0x00FF0000;
    private static final int GREEN_MASK = 0x0000FF00;
    private static final int BLUE_MASK = 0x000000FF;
    private static final int ALPHA_SHIFT = 24;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int BLUE_SHIFT = 0;

    int startColor;
    int endColor;
    int steps;

    public LinearGradient() {
        this.startColor = 0xFF000000;
        this.endColor = 0xFF000000;
        this.steps = 10;
    }

    public LinearGradient(LinearGradient linearGradient) {
        this.startColor = linearGradient.startColor;
        this.endColor = linearGradient.endColor;
        this.steps = linearGradient.steps;
    }

    public LinearGradient from(int color) {
        this.startColor = color;
        return this;
    }

    public LinearGradient to(int color) {
        this.endColor = color;
        return this;
    }

    public LinearGradient withSteps(int steps) {
        Validate.isTrue(steps > 1, "number of steps must be greater than 1");

        this.steps = steps;
        return this;
    }

    public int startColor() {
        return this.startColor;
    }

    public int endColor() {
        return this.endColor;
    }

    public int steps() {
        return this.steps;
    }

    public int colorOfPiece(int piece) {
        if (piece < 1) {
            return this.startColor;
        }
        if (piece >= this.steps) {
            return this.endColor;
        }

        return (0xFF << LinearGradient.ALPHA_SHIFT)
                | (this.redOfPiece(piece) << LinearGradient.RED_SHIFT)
                | (this.greenOfPiece(piece) << LinearGradient.GREEN_SHIFT)
                | (this.blueOfPiece(piece) << LinearGradient.BLUE_SHIFT);
    }

    private int blueOfPiece(int piece) {
        int endBlue = this.blueChannelOf(this.endColor);
        int startBlue = this.blueChannelOf(this.startColor);

        double step = ((double) (endBlue - startBlue)) / (this.steps - 1);

        return (int) (startBlue + (piece * step));
    }

    private int greenOfPiece(int piece) {
        int endGreen = this.greenChannelOf(this.endColor);
        int startGreen = this.greenChannelOf(this.startColor);

        double step = ((double) (endGreen - startGreen)) / (this.steps - 1);

        return (int) (startGreen + (piece * step));
    }

    private int redOfPiece(int piece) {
        int endRed = this.redChannelOf(this.endColor);
        int startRed = this.redChannelOf(this.startColor);

        double step = ((double) (endRed - startRed)) / (this.steps - 1);

        return (int) (startRed + (piece * step));
    }

    private int redChannelOf(int color) {
        return ((color & LinearGradient.RED_MASK) >> LinearGradient.RED_SHIFT);
    }

    private int greenChannelOf(int color) {
        return ((color & LinearGradient.GREEN_MASK) >> LinearGradient.GREEN_SHIFT);
    }

    private int blueChannelOf(int color) {
        return ((color & LinearGradient.BLUE_MASK) >> LinearGradient.BLUE_SHIFT);
    }

}
