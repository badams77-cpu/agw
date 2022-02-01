public class BarometricFormula {

    public static double pressureByHeight(double P0, double T0, double height){
        double pressRoot = Math.pow(P0, (1.0/ (Constants.AIR_INTERNAL_ENERGY_PER_KT+1)));
        double press1 = pressRoot - (Constants.AIR_MOLAR_MASS*Constants.ACCELERATION_DUE_TO_GRAVITY*pressRoot*Constants.RADIUS_EARTH*height)/
                ((Constants.AIR_INTERNAL_ENERGY_PER_KT+1)* Constants.GAS_CONSTANT*T0*(Constants.RADIUS_EARTH+height));
        return Math.pow(press1, 1+Constants.AIR_INTERNAL_ENERGY_PER_KT);
    }

    public static double tempByHeight(double T0, double height){
        double T= T0 * (1.0- (Constants.AIR_MOLAR_MASS*Constants.ACCELERATION_DUE_TO_GRAVITY*Constants.RADIUS_EARTH*height)/
                ( (Constants.AIR_INTERNAL_ENERGY_PER_KT+1)*Constants.GAS_CONSTANT*T0*(Constants.RADIUS_EARTH+height))
                );
        return T;
    }
}
