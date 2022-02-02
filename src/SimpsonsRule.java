public class SimpsonsRule {

    public static double integrate(double a, double b, int N, DoubFunction f) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        double fa = f.eval(a);
        double fb = f.eval(b);
        if (Double.isNaN(fa) ){
          System.err.println(f.getClass().getName() + "IS NaN at "+a);
        }
        if (Double.isNaN(fb)){
            System.err.println(f.getClass().getName() + "IS NaN at "+b);
        }
        // 1/3 terms
        double sum = 1.0 / 3.0 * (fa + fb);


        // 4/3 terms
        for (int i = 1; i < N - 1; i += 2) {
            double x = a + h * i;
            double fx = f.eval(x);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 4.0 / 3.0 * fx;
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = a + h * i;
            double fx = f.eval(x);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 2.0 / 3.0 * fx;
        }

        return sum * h;
    }

}
