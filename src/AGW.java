import java.util.function.DoubleFunction;

public class AGW {

    static int heightStep = 10;
    static int maxHeight = 10000;
    static int NheightStep = maxHeight/heightStep;
    static int latitudeSteps = 100;
    double freqNum;
    static double freqMax = 10000 ;
    static double freqMin = 100;
    static int freqSteps = 1000;


    public static double CO2CONC = 400/1000000;

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
                double absorb = groundintensity*Absorb.absorbH02(concH20, freq, height , (double) heightStep );
                return absorb;
            }
        };

        DoubFunction innerMostC20 = new DoubFunction(){
            // absortPerHeight

            double evalInner( double height,  double params[]  ){
                double freq = params[0];
                double P0 = params[1];
                double T0 = params[2];
                double groundintensity = params[3];
                double P = BarometricFormula.pressureByHeight(P0, T0, height);
                double T = BarometricFormula.tempByHeight(T0, height);
                double concC02 = P*CO2CONC;
                double absorb = groundintensity*Absorb.absorbH02(concC02, freq, height , (double) heightStep );
                return absorb;
            }
        };

        DoubFunction aborbsOverHeightC02 = new DoubFunction(){
            double evalInner( double height,  double params[]  ) {
                double freq = params[0];
                double P0 = params[1];
                double T0 = params[2];
                double intensity = params[3];
                innerMostC20.setParams(freq, P0, T0, intensity);
                return SimpsonsRule.integrate(0, maxHeight, NheightStep, innerMostC20);
            }
        };

        DoubFunction aborbsOverHeightH20 = new DoubFunction(){
            double evalInner( double height,  double params[]  ) {
                double freq = params[0];
                double P0 = params[1];
                double T0 = params[2];
                double intensity = params[3];
                innerMostH20.setParams(freq, P0, T0, intensity);
                return SimpsonsRule.integrate(0, maxHeight, NheightStep, innerMostC20);
            }
        };

        DoubFunction totalAbsorbOverFreqC02 = new DoubFunction() {
            @Override
            double evalInner(double freq, double[] params) {
                double P0 = params[0];
                double T0 = params[1];
                double intensity = PlanckLaw.planck(freq, T0);

                aborbsOverHeightC02.setParams( P0, T0, intensity);
                return SimpsonsRule.integrate(0.0, maxHeight, NheightStep, aborbsOverHeightC02);
            }
        };

        DoubFunction totalAbsorbOverFreqH20 = new DoubFunction() {
            @Override
            double evalInner(double freq, double[] params) {
                double P0 = params[0];
                double T0 = params[1];
                double intensity = PlanckLaw.planck(freq, T0);

                aborbsOverHeightH20.setParams( P0, T0, intensity);
                return SimpsonsRule.integrate(0.0, maxHeight, NheightStep, aborbsOverHeightH20);
            }
        };

        DoubFunction totalAbsorbC02Bylatitude = new DoubFunction() {

            @Override
            double evalInner(double x, double[] params) {
                double lat = x*90/Math.PI;
                double P0 = asp.pressureAtLatitude(lat);
                double T0 = ast.tempAtLatitude(lat);
                totalAbsorbOverFreqC02.setParams( P0, T0);
                return Constants.RADIUS_EARTH*2*lat* SimpsonsRule.integrate(freqMin, freqMax, freqSteps, totalAbsorbOverFreqC02);
            }
        };

        DoubFunction totalAbsorbH20Bylatitude = new DoubFunction() {

            @Override
            double evalInner(double x, double[] params) {
                double lat = x*90/Math.PI;
                double P0 = asp.pressureAtLatitude(lat);
                double T0 = ast.tempAtLatitude(lat);
                totalAbsorbOverFreqH20.setParams( P0, T0);
                return Constants.RADIUS_EARTH*2*lat* SimpsonsRule.integrate(freqMin, freqMax, freqSteps, totalAbsorbOverFreqH20);
            }
        };

        double totalAbsorbC02 = SimpsonsRule.integrate(-Math.PI, Math.PI, latitudeSteps, totalAbsorbC02Bylatitude );

        double totalAbsorbH20 = SimpsonsRule.integrate(-Math.PI, Math.PI, latitudeSteps, totalAbsorbH20Bylatitude);
        double ratio = totalAbsorbC02/ totalAbsorbH20;

        System.out.println( "TotalAborbC02 "+totalAbsorbC02+"\n TotalAbsorbH20 "+totalAbsorbH20 + "\n Ratio "+ratio);
    }

    public static void main(String argv[]){
        calc();
    }

}
