public class AirPressure {

    public double press(double refPress, double height, double refTemp){
        return refPress*Math.exp(-Constants.EARTH_GRAV * Constants.AIR_MASS * height /(Constants.GAS_CONSTANT*refTemp));
    }

}
