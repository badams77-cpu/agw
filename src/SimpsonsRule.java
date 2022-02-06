import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SimpsonsRule {

    private static double third = 1.0/3.0;

    private static int THREADS = 16;

    public static double integrateThreaded(double a, double b, int N, DoubFunction f) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        ForkJoinPool pool = new ForkJoinPool(THREADS);
       AtomicDouble sum = new AtomicDouble();
        boolean isOdd = false;
        for(int i=0; i<N; i++){
            int ii = i;
            double mul = isOdd? 2*third: 4*third;
            if (i==0) { mul = third; }
            if (i==N-1){ mul = third; }
            double mul1 = mul;
            pool.execute( () -> {
                double x = a + h * ii;
                double fi = f.eval(x,ii);
                if (Double.isNaN(fi) ){
                    System.err.println(f.getClass().getName() + "IS NaN at "+x);
                }
                sum.getAndAdd(mul1*fi);
            });
            isOdd = !isOdd;
        }
        try {
            pool.shutdown();
            if (!pool.awaitTermination(1, TimeUnit.HOURS)){
                pool.shutdownNow();
            }
        } catch (Exception e){}
        return sum.getAndAdd(0.0) * h;
    }

    public static double integrate(double a, double b, int N, DoubFunction f) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        double fa = f.eval(a,0);
        double fb = f.eval(b, N-1);
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
            double fx = f.eval(x,i);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 4.0 * third * fx;
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = a + h * i;
            double fx = f.eval(x,i);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 2.0 * third * fx;
        }

        return sum * h;
    }

    public static double integrateConsecutive(double a, double b, int N, DoubFunction f) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        double fa = f.eval(a,0);
        if (Double.isNaN(fa) ){
            System.err.println(f.getClass().getName() + "IS NaN at "+a);
        }

        // 1/3 terms
        double sum = third*fa;


        // 4/3 terms
        boolean isOdd = true;
        for (int i = 1; i < N - 1; i += 1) {
            double x = a + h * i;
            double fx = f.eval(x,i);
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


        double fb = f.eval(b, N-1);
        if (Double.isNaN(fb)){
            System.err.println(f.getClass().getName() + "IS NaN at "+b);
        }
        sum = sum+ fb*third;

        return sum * h;
    }

    public static void main(String args[]){
        DoubFunction func = new DoubFunction() {
            @Override
            double evalInner(double x, double[] params, int i) {
                return 1.0-x*x;
            }
        };
        double consec = integrateConsecutive(-1,1, 1000, func);
        double standard = integrate(-1,1,1000,func);
        double thread = integrateThreaded(-1,1,1000, func);
        System.out.println("Standard Integrator: "+standard);
        System.out.println("Consecutive Integrator: "+consec);
        System.out.println("threaded Integrator: "+thread);
    }

}

class AtomicDouble {
    private AtomicReference<Double> value = new AtomicReference(Double.valueOf(0.0));
    double getAndAdd(double delta) {
        while (true) {
            Double currentValue = value.get();
            Double newValue = Double.valueOf(currentValue.doubleValue() + delta);
            if (value.compareAndSet(currentValue, newValue))
                return currentValue.doubleValue();
        }
    }
}