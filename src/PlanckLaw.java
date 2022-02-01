public class PlanckLaw {

    private static double multiplier = 2.0*Constants.PLANCKS/(Constants.SPEED_OF_LIGHT*Constants.SPEED_OF_LIGHT);

    public static double planck(double freq, double temp){
         double p=multiplier * freq*freq*freq;
         double exponient = Constants.PLANCKS*freq / (Constants.BOLTZMANN*temp);
         if (exponient>100){ return 0; }
         double e = Math.exp(exponient)-1.0;
         if (e==0){ return 0; }
         double a =p/e;
         return a;
    }


}
