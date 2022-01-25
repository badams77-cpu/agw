public class WaterVapourDensity {

    public static double molarDensity(double temp, double pressure){
        return Constants.EARTH_AVERAGE_RELATIVE_HUMIDITY * pressure / ( Constants.GAS_CONSTANT * temp);
    }

}
