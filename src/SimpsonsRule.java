import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SimpsonsRule {

    private static double third = 1.0/3.0;

    private static int THREADS = 1;

    public static double integrateThreaded(double a, double b, int N, DoubFunction f, double ...params) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        ForkJoinPool pool = new ForkJoinPool(THREADS);
       AtomicDouble sum = new AtomicDouble();
        for(int i=0; i<N; i++){
            int ii = i;
            pool.execute( () -> {
                double mul = ii%2==0? 2*third: 4*third;
                if (ii==0) { mul = third; }
                if (ii==N-1){ mul = third; }
                double mul1 = mul;
                double x = a + h * ii;
                double fi = f.eval(x,ii, params);
                if (Double.isNaN(fi) ){
                    System.err.println(f.getClass().getName() + "IS NaN at "+x);
                }
                sum.getAndAdd(mul1*fi);
            });
        }
        try {
            pool.shutdown();
            if (!pool.awaitTermination(1, TimeUnit.HOURS)){
                pool.shutdownNow();
            }
        } catch (Exception e){}
        return sum.getAndAdd(0.0) * h;
    }

    public static double integrate(double a, double b, int N, DoubFunction f, double ...params) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        double fa = f.eval(a,0, params);
        double fb = f.eval(b, N-1, params);
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
            double fx = f.eval(x,i, params);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 4.0 * third * fx;
        }

        // 2/3 terms
        for (int i = 2; i < N - 1; i += 2) {
            double x = a + h * i;
            double fx = f.eval(x,i, params);
            if (Double.isNaN(fx)){
                System.err.println(f.getClass().getName() + "IS NaN at "+x);
            }
            sum += 2.0 * third * fx;
        }

        return sum * h;
    }

    public static double integrateConsecutive(double a, double b, int N, DoubFunction f, double ...params) {         // precision parameter
        double h = (b - a) / (N - 1);     // step size
        double fa = f.eval(a,0, params);
        if (Double.isNaN(fa) ){
            System.err.println(f.getClass().getName() + "IS NaN at "+a);
        }

        // 1/3 terms
        double sum = third*fa;


        // 4/3 terms
        boolean isOdd = true;
        for (int i = 1; i < N - 1; i += 1) {
            double x = a + h * i;
            double fx = f.eval(x,i, params);
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


        double fb = f.eval(b, N-1, params);
        if (Double.isNaN(fb)){
            System.err.println(f.getClass().getName() + "IS NaN at "+b);
        }
        sum = sum+ fb*third;

        return sum * h;
    }

    public static void main(String args[]){
        // Roots of polynumerial to integrate
        List<Number> in = Arrays.asList(-0.9, -0.8,-0.7, -0.6,-0.5, -0.4, -0.3, -0.2, -0.1, 0, .1,.2,.3,.4,.5, .6,.7, .8, .9 );
        DoubFunction func = new DoubFunction() {
            @Override
            double evalInner(double x, double[] params, int i) {
                return in.stream().map(y->y.doubleValue()-x).reduce(1.0,(a,b)->(a*b));
            }
        };
        double consec=0;
        long startConsec = System.currentTimeMillis();
        for(int i=1;i<1000; i++) {
            consec = integrateConsecutive(-1, 1, 100000, func);
        }
        double timeConsec = (System.currentTimeMillis() - startConsec)/1000.0;
        double standard=0;
        long startStandard = System.currentTimeMillis();
        for(int i=1; i<1000; i++) {
                standard = integrate(-1,1,100000,func);
        }
        double timeStandard = (System.currentTimeMillis() - startStandard)/1000.0;
        double threaded=0;
        long startThreaded = System.currentTimeMillis();
        for(int i=1;i<1000; i++) {
            threaded = integrateThreaded(-1, 1, 100000, func);
        }
        double timeThreaded = (System.currentTimeMillis() - startThreaded)/1000.0;
        System.out.println("Standard Integrator: "+standard+" time taken: "+timeStandard+" seconds");
        System.out.println("Consecutive Integrator: "+consec+" time taken: "+timeConsec+" seconds");
        System.out.println("threaded Integrator: "+threaded+" time taken: "+timeThreaded+" seconds");
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