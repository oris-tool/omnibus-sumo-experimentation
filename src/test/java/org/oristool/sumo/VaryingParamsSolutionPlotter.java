package org.oristool.sumo;

import au.com.bytecode.opencsv.CSVWriter;
import org.oristool.sumo.plotter.LineToPlot;
import org.oristool.sumo.plotter.PlotUtils;
import org.oristool.sumo.plotter.Plotter;
import org.oristool.sumo.samplegenerators.UniformSampleGenerator;
import org.oristool.sumo.utils.VaryingParamsSumoAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class VaryingParamsSolutionPlotter {

    public static String SUMO_EXE = "sumo.exe"; // -gui for interface
    public static String WIN_SUMO_BIN = System.getenv("SUMO_HOME") + "\\bin\\" + SUMO_EXE;
    public static String CONFIG_FILE = "simulation\\TramWay\\SumoEnvironment\\configuration.sumo.cfg";
    public static String mainPath = "simulation\\TramWay\\";
    public static final String standardVehConfigFilePath = mainPath + "SumoEnvironment\\vehicles.xml";
    public static String vehConfigFilePath = standardVehConfigFilePath; // XXX agire qui per tempo di reazione
    public static final String noTauVehConfigFilePath = mainPath + "SumoEnvironment\\vehicles_with_no_tau.xml";
    public static String netConfigFilePath = mainPath + "SumoEnvironment\\network.net.xml";
    public static String sensorConfigFilePath = mainPath + "SumoEnvironment\\loops.xml";

    public static BigDecimal maxTramSpeed = new BigDecimal("30");
    public static BigDecimal minTramGap = new BigDecimal("10");
    public static BigDecimal tramLength = new BigDecimal("20");
    public static BigDecimal minVehicleGap = new BigDecimal("0.3");
    public static BigDecimal vehicleLength = new BigDecimal("4.5");

    public static BigDecimal sumo_time_step = new BigDecimal("0.1");
    public static int sumo_runs = 25;
    public static BigDecimal sumo_shift = new BigDecimal("0.66");

    public static List<List<BigDecimal>> arrivalRates = Arrays.asList(
            Arrays.asList(BigDecimal.valueOf(0.025), BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.075)), // prima coda, quattro fasce
            Arrays.asList(BigDecimal.valueOf(0.025), BigDecimal.valueOf(0.2), BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.15)),
            Arrays.asList(BigDecimal.valueOf(0.025), BigDecimal.valueOf(0.3), BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.2))
    );

    public static List<BigDecimal> maxVehicleSpeedsKmh = Arrays.asList(
            new BigDecimal("30"),
            new BigDecimal("30"),
            new BigDecimal("30")
    );

    public static List<BigDecimal> roadLenghts = Arrays.asList(
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(150)
    );

    public static List<BigDecimal> maxVehicleSpeeds =
            maxVehicleSpeedsKmh.stream().map(skmh ->
                    skmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR)
            ).collect(Collectors.toList());

    public static BigInteger timeBound1 = BigInteger.valueOf(440*6);
    public static BigInteger timeBound2 = timeBound1.add(BigInteger.valueOf(220*12));
    public static BigInteger timeBound3 = timeBound2.add(BigInteger.valueOf(330*8));
    public static BigInteger timeBound = timeBound3.add(BigInteger.valueOf(220*12));

    /*
    Info tram 1
 */
    public static BigInteger t1_phaseTime = BigInteger.ZERO;
    public static BigInteger t1_delayEFTime = BigInteger.ZERO;
    public static BigInteger t1_delayLFTime = BigInteger.valueOf(120);
    public static BigInteger t1_crosslightAntTime = BigInteger.valueOf(5);
    public static BigInteger t1_leavingEFTime = BigInteger.valueOf(6);
    public static BigInteger t1_leavingLFTime = BigInteger.valueOf(14);

    /*
        Info tram 2
     */
    public static BigInteger t2_phaseTime = BigInteger.valueOf(40);
    public static BigInteger t2_delayEFTime = BigInteger.ZERO;
    public static BigInteger t2_delayLFTime = BigInteger.valueOf(40);
    public static BigInteger t2_crosslightAntTime = BigInteger.valueOf(5);
    public static BigInteger t2_leavingEFTime = BigInteger.valueOf(6);
    public static BigInteger t2_leavingLFTime = BigInteger.valueOf(14);

    public static int carFlows = 3;


    public static void main(String[] args) throws IOException {

        Date start = new Date();

        int EXPECTED_TRAMS_1 = timeBound.divide(BigInteger.valueOf(220)).intValue() + 2;
        double[][] t1_arrivalTimesPerRun = new double[sumo_runs][EXPECTED_TRAMS_1];

        BigDecimal t1_period_step1 = new BigDecimal(BigInteger.valueOf(440)).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t1_period_step2 = new BigDecimal(BigInteger.valueOf(220)).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t1_period_step3 = new BigDecimal(BigInteger.valueOf(330)).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t1_period_step4 = new BigDecimal(BigInteger.valueOf(220)).divide(sumo_time_step, RoundingMode.HALF_UP);

        BigDecimal t1_phase_step = new BigDecimal(t1_phaseTime).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t1_delayEFT_step = new BigDecimal(t1_delayEFTime).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t1_delayLFT_step = new BigDecimal(t1_delayLFTime).divide(sumo_time_step, RoundingMode.HALF_UP);

        for (int r = 0; r < sumo_runs; r++) {
            double periodStart;
            double periodEnd = 0;
            for (int i = 0; i < t1_arrivalTimesPerRun[r].length; i++) {
                BigDecimal t1_period_step;
                if (periodEnd < timeBound1.doubleValue()) {
                    t1_period_step = t1_period_step1;
                } else if (periodEnd < timeBound2.doubleValue()) {
                    t1_period_step = t1_period_step2;
                } else if (periodEnd < timeBound3.doubleValue()) {
                    t1_period_step = t1_period_step3;
                } else {
                    t1_period_step = t1_period_step4;
                }

                periodStart = periodEnd;
                periodEnd += t1_period_step.doubleValue();

                double arrival_start = periodStart + t1_phase_step.doubleValue();
                t1_arrivalTimesPerRun[r][i] =
                        new UniformSampleGenerator(BigDecimal.valueOf(arrival_start + t1_delayEFT_step.doubleValue()),
                                BigDecimal.valueOf(arrival_start + t1_delayLFT_step.doubleValue()))
                                .getSample().doubleValue();
            }
        }

        int EXPECTED_TRAMS_2 = BigInteger.valueOf(36000).divide(BigInteger.valueOf(220)).intValue() + 2;
        double[][] t2_arrivalTimesPerRun = new double[sumo_runs][EXPECTED_TRAMS_2];

        BigDecimal t2_period_step1 = new BigDecimal(BigInteger.valueOf(440)).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t2_period_step2 = new BigDecimal(BigInteger.valueOf(220)).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t2_period_step3 = new BigDecimal(BigInteger.valueOf(330)).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t2_period_step4 = new BigDecimal(BigInteger.valueOf(220)).divide(sumo_time_step, RoundingMode.HALF_UP);

        BigDecimal t2_phase_step = new BigDecimal(t2_phaseTime).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t2_delayEFT_step = new BigDecimal(t2_delayEFTime).divide(sumo_time_step, RoundingMode.HALF_UP);
        BigDecimal t2_delayLFT_step = new BigDecimal(t2_delayLFTime).divide(sumo_time_step, RoundingMode.HALF_UP);

        for (int r = 0; r < sumo_runs; r++) {
            double periodStart;
            double periodEnd = 0;
            for (int i = 0; i < t2_arrivalTimesPerRun[r].length; i++) {
                BigDecimal t2_period_step;
                if (periodEnd < timeBound2.doubleValue()) {
                    t2_period_step = t2_period_step1;
                } else if (periodEnd < timeBound2.doubleValue()) {
                    t2_period_step = t2_period_step2;
                } else if (periodEnd < timeBound3.doubleValue()) {
                    t2_period_step = t2_period_step3;
                } else {
                    t2_period_step = t2_period_step4;
                }

                periodStart = periodEnd;
                periodEnd += t2_period_step.doubleValue();

                double arrival_start = periodStart + t2_phase_step.doubleValue();
                t2_arrivalTimesPerRun[r][i] =
                        new UniformSampleGenerator(BigDecimal.valueOf(arrival_start + t2_delayEFT_step.doubleValue()),
                                BigDecimal.valueOf(arrival_start + t2_delayLFT_step.doubleValue()))
                                .getSample().doubleValue();
            }
        }

        double INV_STEP_LENGTH = Math.pow(sumo_time_step.doubleValue(), -1);
        int SIMULATION_STEPS = (int) (timeBound.doubleValue() * INV_STEP_LENGTH);

        File resultsFolder = new File("results");
        if (!resultsFolder.exists())
            resultsFolder.mkdir();

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String executionFolderName = dateFormat.format(new Date()) + "_sumo";
        File executionFolder = new File("results/" + executionFolderName);
        executionFolder.mkdir();

        StringBuilder patternBuilder = new StringBuilder();
        String first = "11111111111111111111111119999922222222222222299999000000000000000000000000000000000009999922222222222222299999";
        String second = "11111111111111199999222222222222222222222222299999000000000000000999991111111111111119999922222222222222299999";
        String third = "11111111111111199999222222222222222222222222299999111111111111111999990000000000000009999922222222222222299999";
        String fourth = "22222222222222222222222229999900000000000000099999111111111111111999992222222222222229999911111111111111199999";
        for (int i = 0; i < 24; i++) {
            patternBuilder.append(first);
        }
        for (int i = 0; i < 24; i++) {
            patternBuilder.append(second);
        }
        for (int i = 0; i < 24; i++) {
            patternBuilder.append(third);
        }
        for (int i = 0; i < 24; i++) {
            patternBuilder.append(fourth);
        }
        String pattern = patternBuilder.toString();

        // START - CREAZIONE CARTELLA PATTERN

        String patternFolderName = "varyingParamsSolutionPlot";
        File patternFolder = new File("results/" + executionFolderName + "/" + patternFolderName);
        patternFolder.mkdir();

        // END

        ArrayList<LineToPlot> linesToPlot = new ArrayList<>();
        double[] sumoLinSpace = PlotUtils.getLinSpace(0, timeBound.intValue(), sumo_time_step.doubleValue(), false);

        for (int q = 0; q < carFlows; q++) {
            System.out.print("q");
        }
        for (int q = 0; q < carFlows; q++) {

            CSVWriter queueFileWriter = new CSVWriter(
                    new FileWriter(patternFolder.getAbsolutePath() + "/" + q + ".csv"), '\t'
            );

            boolean[] redPatternForFlow = toSumoRedTrafficLightForFlow(pattern, q, SIMULATION_STEPS);
            VaryingParamsSumoAnalyzer sumoSemAnalyzer = new VaryingParamsSumoAnalyzer();
            sumoSemAnalyzer.analyze(q, INV_STEP_LENGTH, SIMULATION_STEPS, t1_arrivalTimesPerRun,
                    t2_arrivalTimesPerRun, redPatternForFlow, queueFileWriter);

            double[] onQueueCars = sumoSemAnalyzer.getOnQueueCars();
            linesToPlot.add(new LineToPlot("Queue " + q, sumoLinSpace, onQueueCars));

            queueFileWriter.close();

            System.out.print("\b");
        }

        Plotter.plot(executionFolderName + "/plot", "s", "cars", linesToPlot, true, null, true);

        Date end = new Date();

        System.out.println("Duration = " + (end.getTime() - start.getTime()) + " ms");
    }

    private static boolean[] toSumoRedTrafficLightForFlow(String pattern, int carFlow, int simulationSteps) {
        boolean[] redTrafficLightPattern = new boolean[simulationSteps];
        for (int i = 0; i < redTrafficLightPattern.length; i++) {
            redTrafficLightPattern[i] =
                    Integer.parseInt(
                            String.valueOf(
                                    pattern.charAt(
                                            ((int) ((double) i * sumo_time_step.doubleValue())) % pattern.length()
                                    )
                            )
                    )
                            != carFlow;
        }
        return redTrafficLightPattern;
    }
}
