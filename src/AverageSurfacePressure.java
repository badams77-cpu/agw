import java.io.PrintWriter;

public class AverageSurfacePressure {


    // For now we code constant pressure, but the table can take varying Pressure by Latitude
    private PressPoint[] inputTable = {
        new PressPoint(-90.0, 101325),
        new PressPoint(90.0, 101325)
    };

    public double pressureAtLatitude(double lat){
        double leftLat=100;
        double leftPressure=0;
        double rightLat = -100;
        double rightPressure = 0;
        for(int i=0; i<inputTable.length; i++){
            double tabLat = inputTable[i].lat;
            double tabPressure = inputTable[i].pressure;
            if (tabLat<=lat){
                leftPressure = tabPressure;
                leftLat= tabLat;
            }
            if (tabLat>lat){
                rightPressure = tabPressure;
                rightLat = tabLat;
                break;
            }
        }
        double ret1 = leftPressure + (rightPressure-leftPressure) * (lat-leftLat)/(rightLat-leftLat);
        double ret2 = rightPressure - (rightPressure-leftPressure) * (rightLat-lat)/ (rightLat-leftLat);
        return 0.5*(ret1+ret2);
    }

    public static void main(String[] argv){
        PrintWriter pr = new PrintWriter(System.out);
        AverageSurfaceTemperature ast = new AverageSurfaceTemperature();
        for(int i=-90; i<=90; i++){
            pr.println(i+","+ast.tempAtLatitude(i));
        }
        pr.close();
    }

}

class PressPoint {

    double lat;
    double pressure;

    public PressPoint(double lat, double pressure){
        this.lat = lat;
        this.pressure = pressure;
    }

}