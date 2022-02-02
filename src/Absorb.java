public class Absorb {




    public static double absorbC02(double concentration, double freq, double height, double heightStep){
        double c1 = crossSec(freq, Constants.LINE1_C02_EINSTEIN_A , Constants.LINE1_CO2_WN, Constants.LINE1_CO2_MULTIPLICITY, Constants.LINE1_CO2_WIDTH, Constants.LINE1_CO2_NAIR);
        double c2 = crossSec(freq,Constants.LINE2_CO2_EINSTEIN_A , Constants.LINE2_CO2_WN, Constants.LINE2_CO2_MULTIPLICITY, Constants.LINE2_CO2_WIDTH, Constants.LINE2_CO2_NAIR);
        double c3 = crossSec(freq, Constants.LINE3_CO2_EINSTEIN_A, Constants.LINE3_CO2_WN, Constants.LINE3_CO2_MULTIPLICITY, Constants.LINE3_CO2_WIDTH, Constants.LINE3_CO2_NAIR);
        return heightStep*concentration*(c1+c2+c3);
    }

    public static double absorbH02(double concentration, double freq, double height, double heightStep){
        double h1 = crossSec(freq,  Constants.LINE1_H20_EINSTEIN_A , Constants.LINE1_H20_WN, Constants.LINE1_H20_MULTIPLICITY, Constants.LINE1_H20_WIDTH, Constants.LINE1_H20_NAIR);
        double h2 = crossSec(freq, Constants.LINE2_H20_EINSTEIN_A , Constants.LINE2_H20_WN, Constants.LINE2_H20_MULTIPLICITY, Constants.LINE2_H20_WIDTH, Constants.LINE2_H20_NAIR);
        double h3 = crossSec(freq, Constants.LINE3_H20_EINSTEIN_A, Constants.LINE3_H20_WN, Constants.LINE3_H20_MULTIPLICITY, Constants.LINE3_H20_WIDTH, Constants.LINE3_H20_NAIR);
        double h4 =  crossSec(freq, Constants.LINE4_H20_EINSTEIN_A, Constants.LINE4_H20_WN, Constants.LINE4_H20_MULTIPLICITY, Constants.LINE4_H20_WIDTH, Constants.LINE4_H20_NAIR);
        return heightStep*concentration*(h1+h2+h3+h4);
    }

    public static double absorbAll(double concentration, double freq, double heightStep){
        double h1 = crossSec(freq, Constants.LINE1_C02_EINSTEIN_A , Constants.LINE1_H20_WN, Constants.LINE1_H20_MULTIPLICITY, Constants.LINE1_H20_WIDTH, Constants.LINE1_H20_NAIR);
        double h2 = crossSec(freq,Constants.LINE2_H20_EINSTEIN_A , Constants.LINE2_H20_WN, Constants.LINE2_H20_MULTIPLICITY, Constants.LINE2_H20_WIDTH, Constants.LINE2_H20_NAIR);
        double h3 = crossSec(freq, Constants.LINE3_H20_EINSTEIN_A, Constants.LINE3_H20_WN, Constants.LINE3_H20_MULTIPLICITY, Constants.LINE3_H20_WIDTH, Constants.LINE3_H20_NAIR);
        double h4 =  crossSec(freq, Constants.LINE4_H20_EINSTEIN_A, Constants.LINE4_H20_WN, Constants.LINE4_H20_MULTIPLICITY, Constants.LINE4_H20_WIDTH, Constants.LINE4_H20_NAIR);
        double c1 = crossSec(freq, Constants.LINE1_C02_EINSTEIN_A , Constants.LINE1_CO2_WN, Constants.LINE1_CO2_MULTIPLICITY, Constants.LINE1_CO2_WIDTH, Constants.LINE1_CO2_NAIR);
        double c2 = crossSec(freq,Constants.LINE2_CO2_EINSTEIN_A , Constants.LINE2_CO2_WN, Constants.LINE2_CO2_MULTIPLICITY, Constants.LINE2_CO2_WIDTH, Constants.LINE2_CO2_WIDTH);
        double c3 = crossSec(freq,  Constants.LINE3_CO2_EINSTEIN_A, Constants.LINE3_CO2_WN, Constants.LINE3_CO2_MULTIPLICITY, Constants.LINE3_CO2_WIDTH, Constants.LINE3_CO2_WIDTH);
        return Math.exp(-heightStep*concentration*(h1+h2+h3+h4+c1+c2+c3));
    }

    static double crossSec(double freq,  double A, double WN, double g, double gammaair, double nair){
        double pressure = 1.0;
        double temp = 296.0;
        double width = LineWidth.halfwidth(pressure, temp, nair, gammaair)*Constants.SPEED_OF_LIGHT_CM;
        double freq1 = WN*Constants.SPEED_OF_LIGHT_CM;
        double phi = LorentzAbsorb.sigma(freq, freq1, width);
        return  A*phi* freq * freq * freq/ (8*Math.PI* Constants.SPEED_OF_LIGHT*g*Constants.SPEED_OF_LIGHT);
    }



}
