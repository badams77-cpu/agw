import java.util.function.DoubleFunction;

public class AGW {

    static int heightStep = 250;
    static int maxHeight = 25000;
    static int NheightStep = maxHeight/heightStep;
    static int latitudeSteps = 200;
    double freqNum;
    static double freqMax = 9e14 ;
    //static double freqMin = 2e11;
    static double freqMin = 2e11;
    static int freqSteps = 10000;


    public static double CO2CONC = 418.0/(1000000.0*AverageSurfacePressure.AVERAGE_PRESS);

    public static void  calc(){

        AverageSurfacePressure asp = new AverageSurfacePressure();
        AverageSurfaceTemperature ast = new AverageSurfaceTemperature();


        DoubFunction innerMostH20 = new DoubFunction(){
            // absortPerHeight

            double evalInner( double height,  double params[]  , int i){
                double freq = params[0];
                double P0 = params[1];
                double T0 = params[2];
                double lastIntensity = params[3];
                double P = BarometricFormula.pressureByHeight(P0, T0, height);
                double T = BarometricFormula.tempByHeight(T0, height);
                double concH20 = WaterVapourDensity.molarDensity(T, P)/Constants.H2O_MOLECULAR_WEIGHT;
                double exponent = Absorb.absorbH02(concH20, freq, T, height , (double) heightStep );
                double attenuation = Math.exp(-exponent);
                double absorb = lastIntensity*(1.0-attenuation);
                params[3]=lastIntensity*attenuation;
                return absorb;
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
                double exponent = -Absorb.absorbC02(concC02, freq, T, height , (double) heightStep );
                double attenuation = Math.exp(exponent);
                double absorb = lastIntensity*(1.0-attenuation);
                params[3]=lastIntensity*attenuation;
                return absorb;
            }
        };



        DoubFunction absorpsOverHeightH20 = new DoubFunction(){
            double evalInner( double freq,  double params[] ,int i) {
                double P0 = params[0];
                double T0 = params[1];
                double intensity = PlanckLaw.planck(freq, T0);
                innerMostH20.setParams(i, freq, P0, T0, intensity);
                return SimpsonsRule.integrateConsecutive(0, maxHeight, NheightStep, innerMostH20);
            }
        };

        DoubFunction absorpsOverHeightC02 = new DoubFunction(){
            double evalInner( double freq,  double params[] , int i ) {
                double P0 = params[0];
                double T0 = params[1];
                double intensity = PlanckLaw.planck(freq, T0);
                innerMostCO2.setParams(i, freq, P0, T0, intensity);
                return SimpsonsRule.integrateConsecutive(0, maxHeight, NheightStep, innerMostCO2);
            }
        };

        DoubFunction totalAbsorbOverFreqC02 = new DoubFunction() {

            @Override
            double evalInner(double x, double[] params, int i) {
                double lat = x*90/Math.PI;
                double P0 = asp.pressureAtLatitude(lat);
                double T0 = ast.tempAtLatitude(lat);
                System.out.println(i);
                absorpsOverHeightC02.setParams(  i,P0, T0);
                return Constants.RADIUS_EARTH*Constants.RADIUS_EARTH*2.0*Math.cos(x)*
                        SimpsonsRule.integrateThreaded(freqMin, freqMax, freqSteps, absorpsOverHeightC02);
            }
        };

        DoubFunction totalAbsorbOverFreqH20 = new DoubFunction() {
            @Override
            double evalInner(double x, double[] params, int i) {
                double lat = x*90/Math.PI;
                double P0 = asp.pressureAtLatitude(lat);
                double T0 = ast.tempAtLatitude(lat);
                absorpsOverHeightH20.setParams(  i, P0, T0);
                return Constants.RADIUS_EARTH*Constants.RADIUS_EARTH*2.0*Math.cos(x)
                        *SimpsonsRule.integrateThreaded(freqMin, freqMax, freqSteps, absorpsOverHeightH20);
            }
        };

        totalAbsorbOverFreqC02.setNumberOfStep(latitudeSteps);
        absorpsOverHeightC02.setNumberOfStep(freqSteps);
        innerMostCO2.setNumberOfStep(heightStep);
        double totalAbsorbC02 = SimpsonsRule.integrate(-Math.PI, Math.PI, latitudeSteps, totalAbsorbOverFreqC02);
        totalAbsorbOverFreqH20.setNumberOfStep(latitudeSteps);
        absorpsOverHeightH20.setNumberOfStep(freqSteps);
        innerMostH20.setNumberOfStep(heightStep);
        double totalAbsorbH20 = SimpsonsRule.integrate(-Math.PI, Math.PI, latitudeSteps, totalAbsorbOverFreqH20);
        double ratio = totalAbsorbC02/ totalAbsorbH20;

        System.out.println( "Total Absorption C02 "+totalAbsorbC02+"\n Total Absorption H20 "+totalAbsorbH20 + "\n Ratio "+ratio);
    }

    public static void main(String argv[]){
        calc();
    }

}
