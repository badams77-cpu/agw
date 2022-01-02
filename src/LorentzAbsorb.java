public class LorentzAbsorb {

    private static double twopi = Math.PI*2.0;

    public static double sigma(double freq, double center, double width){
        double diff = freq-center;
        double halfWidth = width/2.0;
        return width/(twopi*( diff*diff + halfWidth*halfWidth));
    }

}
