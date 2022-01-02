public class PlanckLaw {

    private static double multiplier = 2.0*Constants.PLANCKS/(Constants.SPEED_OF_LIGHT*Constants.SPEED_OF_LIGHT);

    public double planck(double freq, double temp){
        return multiplier * freq*freq*freq/ ( Math.exp(Constants.PLANCKS*freq / (Constants.BOLTZMANN*temp))-1.0);
    }


}
