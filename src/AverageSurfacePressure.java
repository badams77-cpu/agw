import java.io.PrintWriter;

public class AverageSurfacePressure {

    private TempPoint[] inputTable = {
            new TempPoint(-90,-20.0),
            new TempPoint(-80, -15.0),
            new TempPoint(-70, -8.0),
            new TempPoint(-60, -1.0),
            new TempPoint(-50, 10),
            new TempPoint(-40, 16),
            new TempPoint( -30, 20),
            new TempPoint( -20, 24),
            new TempPoint( -10, 26),
            new TempPoint( 0, 27),
            new TempPoint( 10, 27),
            new TempPoint(20, 25),
            new TempPoint(30,20),
            new TempPoint(40, 17),
            new TempPoint(50,7),
            new TempPoint(60,-1),
            new TempPoint(70, -8),
            new TempPoint(80, -15),
            new TempPoint(90, -17)
    };

    public double pressureAtLatitude(double lat){
        int leftLat=100;
        double leftTemp=0;
        int rightLat = -100;
        double rightTemp = 0;
        for(int i=0; i<inputTable.length; i++){
            int tabLat = inputTable[i].lat;
            double tabTemp = inputTable[i].temp;
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
    double temp;

    public PressPoint(int lat, double temp){
        this.lat = lat;
        this.temp = temp;
    }

}