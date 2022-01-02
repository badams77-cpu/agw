public class SimpsonsRule {

    public static double integrate(double a, double b, int N, DoubFunction f) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size

        // 1/3 terms
        double sum = 1.0 / 3.0 * (f.eval(a) + f.eval(b));

        // 4/3 terms
        for (int i = 1; i < N - 1; i += 2) {
            double x = a + h * i;
            sum += 4.0 / 3.0 * f.eval(x);
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = a + h * i;
            sum += 2.0 / 3.0 * f.eval(x);
        }

        return sum * h;
    }

}
