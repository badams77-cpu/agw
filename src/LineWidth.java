public class LineWidth {

    // https://www.clepair.net/witteman-CO2+IR.pdf

    public static double halfwidth( double pressure, double temp, double nair, double gammaair){
        return 0.5*Math.pow(296.0/temp,nair) * pressure * gammaair;
    }

}
