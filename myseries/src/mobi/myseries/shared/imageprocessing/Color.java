package mobi.myseries.shared.imageprocessing;

public class Color {
    double r;
    double g;
    double b;

    double l;
    double m;
    double s;

    public static Color fromRgb(double r, double g, double b) {
        Color c = new Color();
        c.r = r;
        c.g = g;
        c.b = b;

        c.calculateLms();

        return c;
    }

    public static Color fromLms(double l, double m, double s) {
        Color c = new Color();
        c.l = l;
        c.m = m;
        c.s = s;

        c.calculateRgb();
        return c;
    }

    public double r() {
        return this.r;
    }

    public double g() {
        return this.g;
    }

    public double b() {
        return this.b;
    }

    public double l() {
        return this.l;
    }

    public double m() {
        return this.m;
    }

    public double s() {
        return this.s;
    }

    /*[R]   [ 4.4679 -3.5873  0.1193][L]
     *[G] = [-1.2186  2.3809 -0.1624][M]
     *[B]   [ 0.0497 -0.2439  1.2045][S]
     */
    private void calculateRgb() {
        r = (( 4.4679 * l) - (3.5873 * m)) + (0.1193 * s);
        g = ((-1.2186 * l) + (2.3809 * m)) - (0.1624 * s);
        b = (( 0.0497 * l) - (0.2439 * m)) + (1.2045 * s);
    }

    /*[L]   [0.3811 0.5783 0.0402][R]
     *[M] = [0.1967 0.7244 0.0782][G]
     *[S]   [0.0241 0.1288 0.8444][B]
     */
    private void calculateLms() {
        l = (0.3811 * r) + (0.5783 * g) + (0.0402 * b);
        m = (0.1967 * r) + (0.7244 * g) + (0.0782 * b);
        s = (0.0241 * r) + (0.1288 * g) + (0.8444 * b);

    }
}
