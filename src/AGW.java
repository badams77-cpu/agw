import java.util.function.DoubleFunction;

public class AGW {

    static int heightStep = 100;
    static int maxHeight = 25000;
    static int NheightStep = maxHeight/heightStep;
    static int latitudeSteps = 100;
    double freqNum;
    static double freqMax = 3e14 ;
    //static double freqMin = 2e11;
    static double freqMin = 6.5e13;
    static int freqSteps = 1000;


    public static double CO2CONC = 400.0/(1000000.0*AverageSurfacePressure.AVERAGE_PRESS);

    public static void  calc(){

        AverageSurfacePressure asp = new AverageSurfacePressure();
        AverageSurfaceTemperature ast = new AverageSurfaceTemperature();


        DoubFunction innerMostH20 = new DoubFunction(){
            // absortPerHeight

            double evalInner( double height,  double params[]  ){
                double freq = params[0];
                double P0 = params[1];
                double T0 = params[2];
                double groundintensity = params[3];
                double P = BarometricFormula.pressureByHeight(P0, T0, height);
                double T = BarometricFormula.tempByHeight(T0, height);
                double concH20 = WaterVapourDensity.molarDensity(T, P);
                if (concH20>10){
                    System.out.println("x");
                }
                double absorb = groundintensity*Absorb.absorbH02(concH20, freq, height , (double) heightStep );
                return absorb;
            }
        };

        DoubFunction innerMostCO2 = new DoubFunction(){
            // absortPerHeight

            double evalInner( double height,  double params[]  ){
                double freq = params[0];
                double P0 = params[1];
                double T0 = params[2];
                double groundintensity = params[3];
                double P = BarometricFormula.pressureByHeight(P0, T0, height);
                double T = BarometricFormula.tempByHeight(T0, height);
                double concC02 = P*CO2CONC/(Constants.GAS_CONSTANT*T);
                double absorb = groundintensity*Absorb.absorbC02(concC02, freq, height , (double) heightStep );
                return absorb;
            }
        };

        DoubFunction aborbsOverHeightC02 = new DoubFunction(){
            double evalInner( double freq,  double params[]  ) {
                double P0 = params[0];
                double T0 = params[1];
                double intensity = PlanckLaw.planck(freq, T0);
                innerMostCO2.setParams(freq, P0, T0, intensity);
                return SimpsonsRule.integrate(0, maxHeight, NheightStep, innerMostCO2);
            }
        };

        DoubFunction aborbsOverHeightH20 = new DoubFunction(){
            double evalInner( double freq,  double params[]  ) {
                double P0 = params[0];
                double T0 = params[1];
                double intensity = PlanckLaw.planck(freq, T0);
                innerMostH20.setParams(freq, P0, T0, intensity);
                return SimpsonsRule.integrate(0, maxHeight, NheightStep, innerMostH20);
            }
        };

        DoubFunction totalAbsorbOverFreqC02 = new DoubFunction() {
            @Override
            double evalInner(double x, double[] params) {
                double lat = x*90/Math.PI;
                double P0 = asp.pressureAtLatitude(lat);
                double T0 = ast.tempAtLatitude(lat);
                aborbsOverHeightC02.setParams(  P0, T0);
                return Constants.RADIUS_EARTH*2*Math.cos(x)*SimpsonsRule.integrate(freqMin, freqMax, freqSteps, aborbsOverHeightC02);
            }
        };

        DoubFunction totalAbsorbOverFreqH20 = new DoubFunction() {
            @Override
            double evalInner(double x, double[] params) {
                double lat = x*90/Math.PI;
                double P0 = asp.pressureAtLatitude(lat);
                double T0 = ast.tempAtLatitude(lat);

                aborbsOverHeightH20.setParams(  P0, T0);
                return Constants.RADIUS_EARTH*2*Math.cos(x)*SimpsonsRule.integrate(freqMin, freqMax, freqSteps, aborbsOverHeightH20);
            }
        };


        double totalAbsorbC02 = SimpsonsRule.integrate(-Math.PI, Math.PI, latitudeSteps, totalAbsorbOverFreqC02);

        double totalAbsorbH20 = SimpsonsRule.integrate(-Math.PI, Math.PI, latitudeSteps, totalAbsorbOverFreqH20);
        double ratio = totalAbsorbC02/ totalAbsorbH20;

        System.out.println( "Total Absorption C02 "+totalAbsorbC02+"\n Total Absorption H20 "+totalAbsorbH20 + "\n Ratio "+ratio);
    }

    public static void main(String argv[]){
        calc();
    }

}
