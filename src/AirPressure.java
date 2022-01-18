public class AirPressure {

    public static double pressByAltitude(double refPress, double height, double refTemp){
        return refPress*Math.exp(-Constants.EARTH_GRAV * Constants.AIR_MASS * height /(Constants.GAS_CONSTANT*refTemp));
    }


//https://www.translatorscafe.com/unit-converter/en-US/calculator/altitude/
    public static double density(double pressure, double temp){
        return pressure*101325/(287.052*temp);
    }
// http://www.cohp.org/ak/notes/pressure_altitude_simplified.html
    public static double pressureByAltitude(double refPress, double surfaceTemp, double height ){
        return refPress*Math.pow(1+ 0.0065*height /surfaceTemp, 5.256);
    }

    public static double tempByAltitude(double surfaceTemp, double height){
        return surfaceTemp - 0.0098 * height;
    }
}
