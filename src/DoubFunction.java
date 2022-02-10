public abstract class DoubFunction {


    abstract double evalInner(double x, double params[], int i);

    public double eval(double x, int i, double ...params){
        return evalInner(x,  params, i);
    }

}
