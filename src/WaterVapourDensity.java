import java.awt.geom.QuadCurve2D;

public class WaterVapourDensity {

    public static double molarDensity(double temp, double pressure){
        if (temp<243.04){ return 0.0; }
        double statPress = 610.94 * Math.exp( 17.625*(temp-273.0) / (temp+243.04-273));
        return Constants.EARTH_AVERAGE_RELATIVE_HUMIDITY * statPress / ( Constants.GAS_CONSTANT * temp);
    }

}
