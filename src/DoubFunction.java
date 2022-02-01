public abstract class DoubFunction {

    private double[] extraParams;

    void setParams(double ...extraParams){
        this.extraParams = extraParams;
    }

    abstract double evalInner(double x, double params[]);

    public double eval(double x){
        return evalInner(x, extraParams);
    }

}
