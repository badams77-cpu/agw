import java.io.PrintWriter;

public class AverageSurfacePressure {


    // For now we code constant pressure, but the table can take varying Pressure by Latitude
    private PressPoint[] inputTable = {
        new PressPoint(-90, 101325),
        new PressPoint(90, 101325)
    };

    public double pressureAtLatitude(double lat){
        int leftLat=100;
        double leftTemp=0;
        int rightLat = -100;
        double rightTemp = 0;
        for(int i=0; i<inputTable.length; i++){
            int tabLat = inputTable[i].lat;
            double tabTemp = inputTable[i].pressure;
            if (tabLat<=lat){
                leftTemp = tabTemp;
                leftLat= tabLat;
            }
            if (tabLat>lat){
                rightTemp = tabTemp;
                rightLat = tabLat;
                break;
            }
        }
        double ret1 = leftTemp + (rightTemp-leftTemp) * (lat-leftLat)/(rightLat-leftLat);
        double ret2 = rightTemp - (rightTemp-leftTemp) * (rightLat-lat)/ (rightLat-leftLat);
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

    int lat;
    double pressure;

    public PressPoint(int lat, double temp){
        this.lat = lat;
        this.pressure = pressure;
    }

}