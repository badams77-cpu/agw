public abstract class DoubFunction {

    private double[][] extraParams;

    abstract double evalInner(double x, double params[], int i);

    public double eval(double x, int i, double ...params){
        return evalInner(x,  params, i);
    }

}
