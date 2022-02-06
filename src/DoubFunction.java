public abstract class DoubFunction {

    private double[][] extraParams;

    void setNumberOfStep(int n){
        extraParams = new double[n][];
    }

    void setParams(int i, double ...extraParams){
        this.extraParams[i] = extraParams;
    }

    abstract double evalInner(double x, double params[], int i);

    public double eval(double x, int i){
        return evalInner(x, extraParams[i], i);
    }

}
