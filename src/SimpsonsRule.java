public class SimpsonsRule {

    private static double third = 1.0/3.0;

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
        double sum = third * (fa + fb);


        // 4/3 terms
        for (int i = 1; i < N - 1; i += 2) {
            double x = a + h * i;
            double fx = f.eval(x);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 4.0 * third * fx;
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = a + h * i;
            double fx = f.eval(x);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 2.0 * third * fx;
        }

        return sum * h;
    }

    public static double integrateConsective(double a, double b, int N, DoubFunction f) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        double fa = f.eval(a);

        if (Double.isNaN(fa) ){
            System.err.println(f.getClass().getName() + "IS NaN at "+a);
        }

        // 1/3 terms
        double sum = third*fa;


        // 4/3 terms
        boolean isOdd = true;
        for (int i = 1; i < N - 1; i += 1) {
            double x = a + h * i;
            double fx = f.eval(x);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            if (isOdd) {
                sum += 4.0 * third * fx;
            } else {
                sum += 2.0 * third * fx;
            }
            isOdd=!isOdd;
        }


        double fb = f.eval(b);
        if (Double.isNaN(fb)){
            System.err.println(f.getClass().getName() + "IS NaN at "+b);
        }
        sum = sum+ fb*third;

        return sum * h;
    }

}
