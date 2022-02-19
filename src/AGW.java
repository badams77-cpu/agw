import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.DoubleFunction;

public class AGW {

    public static int heightStep = 10;
    public static int maxHeight = 25000;
    public static int NheightStep = maxHeight/heightStep;
    public static int latitudeSteps = 100;
    public static double freqMax = 1.0e15 ;
    public static double freqMin = 1e11;
    public static int freqSteps = 50000;

    public static PrintWriter out;


    public static double CO2CONC = 800.0/(1000000.0);

    public static void  calc(){

        AverageSurfacePressure asp = new AverageSurfacePressure();
        AverageSurfaceTemperature ast = new AverageSurfaceTemperature();
        out.println("Parts per million C02, Absorb C02 Watts, Absorb H20 Watts, Absorb Both Watts");
        for(int conc=100;conc<1200;conc+=100) {
            CO2CONC = conc/1000000.0;

            DoubFunction innerMostH20 = new DoubFunction(){
                // absortPerHeight

                double evalInner( double height,  double params[]  , int i){
                    double freq = params[0];
                    double P0 = params[1];
                    double T0 = params[2];
                    double P = BarometricFormula.pressureByHeight(P0, T0, height);
                    double T = BarometricFormula.tempByHeight(T0, height);
                    double concH20 = WaterVapourDensity.molarDensity(T, P)/Constants.H2O_MOLECULAR_WEIGHT;
                    return Absorb.absorbH02(concH20, freq, T, P,  height , (double) heightStep );

                }
            };

            DoubFunction innerMostCO2 = new DoubFunction(){
                // absortPerHeight

                double evalInner( double height,  double params[] , int i ){
                    double freq = params[0];
                    double P0 = params[1];
                    double T0 = params[2];
                    double lastIntensity = params[3];
                    double P = BarometricFormula.pressureByHeight(P0, T0, height);
                    double T = BarometricFormula.tempByHeight(T0, height);
                    double concC02 = P*CO2CONC/(Constants.GAS_CONSTANT*T*Constants.CO2_MOLECULAR_WEIGHT);
                    return Absorb.absorbC02(concC02, freq, T, P,  height , (double) heightStep );

                }
            };

            DoubFunction innerMostBoth = new DoubFunction(){
                // absortPerHeight

                double evalInner( double height,  double params[] , int i ){
                    double freq = params[0];
                    double P0 = params[1];
                    double T0 = params[2];
                    double P = BarometricFormula.pressureByHeight(P0, T0, height);
                    double T = BarometricFormula.tempByHeight(T0, height);
                    double concH20 = WaterVapourDensity.molarDensity(T, P)/Constants.H2O_MOLECULAR_WEIGHT;
                    double concC02 = P*CO2CONC/(Constants.GAS_CONSTANT*T*Constants.CO2_MOLECULAR_WEIGHT);
                    return Absorb.absorbBoth(concH20, concC02, freq, T,P,  height , (double) heightStep );

                }
            };

            DoubFunction justPlank = new DoubFunction() {
                @Override
                double evalInner(double freq, double[] params, int i) {
                    double P0 = params[0];
                    double T0 = params[1];
                    double intensity = PlanckLaw.planck(freq, T0);
                    return intensity;
                }
            };

            DoubFunction absorpsOverHeightH20 = new DoubFunction(){
                double evalInner( double freq,  double params[] ,int i) {
                    double P0 = params[0];
                    double T0 = params[1];
                    double intensity = PlanckLaw.planck(freq, T0);
                    return intensity*(1.0-Math.exp(-SimpsonsRule.integrateConsecutive(0, maxHeight, NheightStep, innerMostH20, freq, P0, T0, intensity)));
                }
            };

            DoubFunction absorpsOverHeightC02 = new DoubFunction(){
                double evalInner( double freq,  double params[] , int i ) {
                    double P0 = params[0];
                    double T0 = params[1];
                    double intensity = PlanckLaw.planck(freq, T0);
                    return intensity*(1.0-Math.exp(-SimpsonsRule.integrateConsecutive(0, maxHeight, NheightStep, innerMostCO2, freq, P0, T0, intensity)));
                }
            };

            DoubFunction absorpOverHeightBoth = new DoubFunction(){
                double evalInner( double freq,  double params[] , int i ) {
                    double P0 = params[0];
                    double T0 = params[1];
                    double intensity = PlanckLaw.planck(freq, T0);
                    return intensity*(1.0-Math.exp(-SimpsonsRule.integrateConsecutive(0, maxHeight, NheightStep, innerMostBoth, freq, P0, T0, intensity)));
                }
            };

            DoubFunction totalAbsorbOverFreqC02 = new DoubFunction() {

                @Override
                double evalInner(double x, double[] params, int i) {
                    double lat = x*90/Math.PI;
                    double P0 = asp.pressureAtLatitude(lat);
                    double T0 = ast.tempAtLatitude(lat);
                    return 2.0*Math.PI*Constants.RADIUS_EARTH*Constants.RADIUS_EARTH*Math.cos(x)
                            *SimpsonsRule.integrateThreaded(freqMin, freqMax, freqSteps, absorpsOverHeightC02, P0, T0);
                }
            };

            DoubFunction totalOverPlanck = new DoubFunction() {

                @Override
                double evalInner(double x, double[] params, int i) {
                    double lat = x*90/Math.PI;
                    double P0 = asp.pressureAtLatitude(lat);
                    double T0 = ast.tempAtLatitude(lat);
                    return 2.0*Math.PI*Constants.RADIUS_EARTH*Constants.RADIUS_EARTH*Math.cos(x)
                            *SimpsonsRule.integrateThreaded(freqMin, freqMax, freqSteps, justPlank, P0, T0);
                }
            };


            DoubFunction totalAbsorbOverFreqH20 = new DoubFunction() {
                @Override
                double evalInner(double x, double[] params, int i) {
                    double lat = x*180/Math.PI;
                    double P0 = asp.pressureAtLatitude(lat);
                    double T0 = ast.tempAtLatitude(lat);
                    return 2.0*Math.PI*Constants.RADIUS_EARTH*Constants.RADIUS_EARTH*Math.cos(x)
                            *SimpsonsRule.integrateThreaded(freqMin, freqMax, freqSteps, absorpsOverHeightH20, P0, T0);
                }
            };

            DoubFunction totalAbsorbOverFreqBoth = new DoubFunction() {
                @Override
                double evalInner(double x, double[] params, int i) {
                    double lat = x*180/Math.PI;
                    double P0 = asp.pressureAtLatitude(lat);
                    double T0 = ast.tempAtLatitude(lat);
                    return 2.0*Math.PI*Constants.RADIUS_EARTH*Constants.RADIUS_EARTH*Math.cos(x)
                            *SimpsonsRule.integrateThreaded(freqMin, freqMax, freqSteps, absorpOverHeightBoth, P0, T0);
                }
            };
            // Loop over parts per million of CO2

            double totalAbsorbC02 = SimpsonsRule.integrate(-Math.PI / 2.0, Math.PI / 2.0, latitudeSteps, totalAbsorbOverFreqC02);
            double totalPlanck = SimpsonsRule.integrate(-Math.PI / 2.0, Math.PI / 2.0, latitudeSteps, totalOverPlanck);
            double totalAbsorbH20 = SimpsonsRule.integrate(-Math.PI / 2.0, Math.PI / 2.0, latitudeSteps, totalAbsorbOverFreqH20);
            double totalAbsorbBoth = SimpsonsRule.integrate(-Math.PI / 2.0, Math.PI / 2.0, latitudeSteps, totalAbsorbOverFreqBoth);
            double ratio = totalAbsorbC02 / totalAbsorbH20;
            double areaEarth = 4.0 * Math.PI * Constants.RADIUS_EARTH * Constants.RADIUS_EARTH;

            System.out.println("Total Absorption C02 " + totalAbsorbC02 + "\n Total Absorption H20 " + totalAbsorbH20 + "\n Ratio " + ratio);
            System.out.println("Total Absorption Both " + totalAbsorbBoth);
            System.out.println("Total Radiated Light At Surface " + (totalPlanck));
            System.out.println("Total Radiated Light " + (totalPlanck - totalAbsorbBoth));
            System.out.println("Total Absorption C02 per meter " + (totalAbsorbC02 / areaEarth) +
                    "\n Total Absorption H20 per meter " + (totalAbsorbH20 / areaEarth) + "\n Ratio " + ratio);
            System.out.println("Total Absorption Both per meter " + (totalAbsorbBoth / areaEarth));
            System.out.println("Total Radiated Light At Surface per meter " + (totalPlanck / areaEarth));
            System.out.println("Total Radiated Light per meter " + ((totalPlanck - totalAbsorbBoth) / areaEarth));
            out.println(conc+","+  (totalAbsorbC02 / areaEarth)+","+(totalAbsorbH20 / areaEarth)+","+(totalAbsorbBoth / areaEarth));
        }
    }

    public static void main(String argv[]){
        try {
            FileOutputStream fout = new FileOutputStream(new File("absorb1.csv"));
            out = new PrintWriter(fout);
            calc();
          out.close();
            fout.close();
        } catch (IOException e){
            e.printStackTrace(System.out);
       }
    }

}
