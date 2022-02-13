public class Absorb {




    public static double absorbC02(double concentration, double freq,double temp, double height, double heightStep){
        double c1 = crossSec(freq, temp, Constants.LINE1_C02_EINSTEIN_A , Constants.LINE1_CO2_WN, Constants.LINE1_CO2_MULTIPLICITY, Constants.LINE1_CO2_WIDTH, Constants.LINE1_CO2_NAIR);
        double c2 = crossSec(freq, temp, Constants.LINE2_CO2_EINSTEIN_A , Constants.LINE2_CO2_WN, Constants.LINE2_CO2_MULTIPLICITY, Constants.LINE2_CO2_WIDTH, Constants.LINE2_CO2_NAIR);
        double c3 = crossSec(freq, temp, Constants.LINE3_CO2_EINSTEIN_A, Constants.LINE3_CO2_WN, Constants.LINE3_CO2_MULTIPLICITY, Constants.LINE3_CO2_WIDTH, Constants.LINE3_CO2_NAIR);
        return concentration*(c1+c2+c3);
    }

    public static double absorbH02(double concentration, double freq,double temp, double height, double heightStep){
        double h1 = crossSec(freq, temp, Constants.LINE1_H20_EINSTEIN_A , Constants.LINE1_H20_WN, Constants.LINE1_H20_MULTIPLICITY, Constants.LINE1_H20_WIDTH, Constants.LINE1_H20_NAIR);
        double h2 = crossSec(freq,  temp, Constants.LINE2_H20_EINSTEIN_A , Constants.LINE2_H20_WN, Constants.LINE2_H20_MULTIPLICITY, Constants.LINE2_H20_WIDTH, Constants.LINE2_H20_NAIR);
        double h3 = crossSec(freq, temp, Constants.LINE3_H20_EINSTEIN_A, Constants.LINE3_H20_WN, Constants.LINE3_H20_MULTIPLICITY, Constants.LINE3_H20_WIDTH, Constants.LINE3_H20_NAIR);
        double h4 =  crossSec(freq, temp, Constants.LINE4_H20_EINSTEIN_A, Constants.LINE4_H20_WN, Constants.LINE4_H20_MULTIPLICITY, Constants.LINE4_H20_WIDTH, Constants.LINE4_H20_NAIR);
        return concentration*(h1+h2+h3+h4);
    }

    public static double absorbBoth(double concH20, double concC02, double freq,double temp, double height, double heightStep){
        double c1 = crossSec(freq, temp, Constants.LINE1_C02_EINSTEIN_A , Constants.LINE1_CO2_WN, Constants.LINE1_CO2_MULTIPLICITY, Constants.LINE1_CO2_WIDTH, Constants.LINE1_CO2_NAIR);
        double c2 = crossSec(freq, temp, Constants.LINE2_CO2_EINSTEIN_A , Constants.LINE2_CO2_WN, Constants.LINE2_CO2_MULTIPLICITY, Constants.LINE2_CO2_WIDTH, Constants.LINE2_CO2_NAIR);
        double c3 = crossSec(freq, temp, Constants.LINE3_CO2_EINSTEIN_A, Constants.LINE3_CO2_WN, Constants.LINE3_CO2_MULTIPLICITY, Constants.LINE3_CO2_WIDTH, Constants.LINE3_CO2_NAIR);

        double h1 = crossSec(freq, temp, Constants.LINE1_H20_EINSTEIN_A , Constants.LINE1_H20_WN, Constants.LINE1_H20_MULTIPLICITY, Constants.LINE1_H20_WIDTH, Constants.LINE1_H20_NAIR);
        double h2 = crossSec(freq,  temp, Constants.LINE2_H20_EINSTEIN_A , Constants.LINE2_H20_WN, Constants.LINE2_H20_MULTIPLICITY, Constants.LINE2_H20_WIDTH, Constants.LINE2_H20_NAIR);
        double h3 = crossSec(freq, temp, Constants.LINE3_H20_EINSTEIN_A, Constants.LINE3_H20_WN, Constants.LINE3_H20_MULTIPLICITY, Constants.LINE3_H20_WIDTH, Constants.LINE3_H20_NAIR);
        double h4 =  crossSec(freq, temp, Constants.LINE4_H20_EINSTEIN_A, Constants.LINE4_H20_WN, Constants.LINE4_H20_MULTIPLICITY, Constants.LINE4_H20_WIDTH, Constants.LINE4_H20_NAIR);
        return concC02*(c1+c2+c3)+ concH20*(h1+h2+h3+h4);
    }

    static double crossSec(double freq,  double temp, double A, double WN, double g, double gammaair, double nair){
        double pressure = 1.0;
        double width = LineWidth.halfwidth(pressure, temp, nair, gammaair)*Constants.SPEED_OF_LIGHT_CM;
        double freq1 = WN*Constants.SPEED_OF_LIGHT_CM;
        double phi = LorentzAbsorb.sigma(freq, freq1, width);
        return  A*phi* freq * freq * freq/ (8*Math.PI* Constants.SPEED_OF_LIGHT*g*Constants.SPEED_OF_LIGHT);
    }



}
