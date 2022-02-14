public class LorentzAbsorb {

    private static double twopi = Math.PI*2.0;

    public static double sigma(double freq, double center, double width){
        double diff = freq-center;
        return width/(Math.PI*( diff*diff + width*width));
    }

}
