/********************************************************************************
 * This program is part of a software application using SUMO
 * (Simulation of Urban MObility, see https://eclipse.org/sumo)
 * to analyze multimodal urban intersections.
 * 
 * Copyright (C) 2022-2023 Software Technologies Lab, University of Florence. 
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package org.oristool.sumo;

import au.com.bytecode.opencsv.CSVWriter;
import org.oristool.sumo.samplegenerators.UniformSampleGenerator;
import org.oristool.sumo.utils.*;
import org.oristool.sumo.utils.pattern.SemPatternGenerator;
import org.oristool.sumo.utils.pattern.VehicleFlow;
import org.oristool.sumo.utils.pc.Table2PCSumoAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Table3PatternComparator {

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
    public static int hyperPeriods = 5;
    public static BigDecimal sumo_shift = new BigDecimal("0.66");

    private static List<Integer> semSlotDurations = Arrays.asList(15, 25, 35);
    private static int redTime = 5;

    public static void main(String[] args) throws IOException {

        ScenarioDefiner.roadLenghts = Arrays.asList(
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(150)
        );

        ScenarioDefiner.arrivalRates = Arrays.asList(
                BigDecimal.valueOf(0.05),
                BigDecimal.valueOf(0.1),
                BigDecimal.valueOf(0.15)
        );

        ScenarioDefiner.maxVehicleSpeedsKmh = Arrays.asList(
                new BigDecimal("50"),
                new BigDecimal("50"),
                new BigDecimal("50")
        );

        ScenarioDefiner.updateFields();

        Date start = new Date();

        BigInteger timeBound = BigInteger.valueOf(Utils.lcm(
                        ScenarioDefiner.semPeriod.intValue(),
                        ScenarioDefiner.t1_periodTime.intValue(),
                        ScenarioDefiner.t2_periodTime.intValue()))
                .multiply(BigInteger.valueOf(hyperPeriods));

        /*
         * Tempi di arrivo del primo tram per tutti i run, per tutti i pattern, per tutte le code.
         */
        int EXPECTED_TRAMS_1 = timeBound.divide(ScenarioDefiner.t1_periodTime).intValue() + 2;
        double[][] t1_arrivalTimesPerRun = new double[sumo_runs][EXPECTED_TRAMS_1];
        BigDecimal t1_period_step = new BigDecimal(ScenarioDefiner.t1_periodTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        BigDecimal t1_phase_step = new BigDecimal(ScenarioDefiner.t1_phaseTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        BigDecimal t1_delayEFT_step = new BigDecimal(ScenarioDefiner.t1_delayEFTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        BigDecimal t1_delayLFT_step = new BigDecimal(ScenarioDefiner.t1_delayLFTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        for (int r = 0; r < sumo_runs; r++) {
            for (int i = 0; i < t1_arrivalTimesPerRun[r].length; i++) {
                double arrival_start = Double.parseDouble(Integer.toString(i)) * t1_period_step.doubleValue()
                        + t1_phase_step.doubleValue();
                t1_arrivalTimesPerRun[r][i] =
                        new UniformSampleGenerator(BigDecimal.valueOf(arrival_start + t1_delayEFT_step.doubleValue()),
                                BigDecimal.valueOf(arrival_start + t1_delayLFT_step.doubleValue()))
                                .getSample().doubleValue();
            }
        }

        /*
         * Tempi di arrivo del secondo tram per tutti i run, per tutti i pattern, per tutte le code.
         */
        int EXPECTED_TRAMS_2 = timeBound.divide(ScenarioDefiner.t2_periodTime).intValue() + 2;
        double[][] t2_arrivalTimesPerRun = new double[sumo_runs][EXPECTED_TRAMS_2];
        BigDecimal t2_period_step = new BigDecimal(ScenarioDefiner.t2_periodTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        BigDecimal t2_phase_step = new BigDecimal(ScenarioDefiner.t2_phaseTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        BigDecimal t2_delayEFT_step = new BigDecimal(ScenarioDefiner.t2_delayEFTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        BigDecimal t2_delayLFT_step = new BigDecimal(ScenarioDefiner.t2_delayLFTime).divide(sumo_time_step,
                RoundingMode.HALF_UP);
        for (int r = 0; r < sumo_runs; r++) {
            for (int i = 0; i < t2_arrivalTimesPerRun[r].length; i++) {
                double arrival_start = Double.parseDouble(Integer.toString(i)) * t2_period_step.doubleValue()
                        + t2_phase_step.doubleValue();
                t2_arrivalTimesPerRun[r][i] =
                        new UniformSampleGenerator(BigDecimal.valueOf(arrival_start + t2_delayEFT_step.doubleValue()),
                                BigDecimal.valueOf(arrival_start + t2_delayLFT_step.doubleValue()))
                                .getSample().doubleValue();
            }
        }

        double INV_STEP_LENGTH = Math.pow(sumo_time_step.doubleValue(), -1);
        int SIMULATION_STEPS = (int) (timeBound.doubleValue() * INV_STEP_LENGTH);

        double bestOccupationPercentage = Double.MAX_VALUE;
        String bestPattern = null;

        // PREDISPOSIZIONE PATTERN DA ANALIZZARE

        List<VehicleFlow> vehicleFlows = new ArrayList<>();
        for (int i = 0; i < ScenarioDefiner.carFlows; i++) {
            vehicleFlows.add(new VehicleFlow(Integer.toString(i), semSlotDurations));
        }

        ArrayList<String> patternsToAnalyze = new ArrayList<>();
        SemPatternGenerator
                .generateAllPatternWithGreenSlotSets(vehicleFlows, ScenarioDefiner.semPeriod.intValue(), redTime)
                .forEach(sp -> patternsToAnalyze.add(sp.getSchedule()));

        System.out.println(LocalDateTime.now());

        System.out.println("da analizzare " + patternsToAnalyze.size() + " pattern semaforici ");
        for (int i = 0; i < patternsToAnalyze.size(); i++) {
            System.out.print("x");
        }

        // START - CREAZIONE CARTELLA RISULTATI E FILE LISTA PATTERN

        File resultsFolder = new File("results");
        if (!resultsFolder.exists())
            resultsFolder.mkdir();

        String executionFolderName = "table3_sumo";
        File executionFolder = new File("results/" + executionFolderName);
        executionFolder.mkdir();

        CSVWriter patternListWriter = new CSVWriter(
                new FileWriter("results/" + executionFolderName + "/" + executionFolderName + ".csv"), '\t'
        );

        // END

        for (int i = 0; i < patternsToAnalyze.size(); i++) {

            String pattern = patternsToAnalyze.get(i);

            // START - CREAZIONE CARTELLA PATTERN

            String patternFolderName = Integer.toString(i);
            File patternFolder = new File("results/" + executionFolderName + "/" + patternFolderName);
            patternFolder.mkdir();

            // END

            for (int q = 0; q < ScenarioDefiner.carFlows; q++) {
                System.out.print("q");
            }
            double worstPatternOccupationPercentage = Double.MIN_VALUE;
            for (int q = 0; q < ScenarioDefiner.carFlows; q++) {

                // START - CREAZIONE FILE CODA

                CSVWriter queueFileWriter = new CSVWriter(
                        new FileWriter(patternFolder.getAbsolutePath() + "/" + q + ".csv"), '\t'
                );

                // END

                boolean[] redPatternForFlow = toSumoRedTrafficLightForFlow(pattern, q, SIMULATION_STEPS);
                Table2PCSumoAnalyzer sumoSemAnalyzer = new Table2PCSumoAnalyzer();
                sumoSemAnalyzer.analyze(q, timeBound, INV_STEP_LENGTH, SIMULATION_STEPS, t1_arrivalTimesPerRun,
                        t2_arrivalTimesPerRun, redPatternForFlow, queueFileWriter);
                double occupationPercentage = BigDecimal.valueOf(sumoSemAnalyzer.getMaxCarsOnQueue())
                        .divide(new BigDecimal(ScenarioDefiner.maxQueueSizes.get(q)), 3, RoundingMode.HALF_DOWN)
                        .doubleValue();
                worstPatternOccupationPercentage = Double.max(occupationPercentage, worstPatternOccupationPercentage);

                queueFileWriter.close();

                System.out.print("\b");
            }

            patternListWriter.writeNext(List.of(
                    patternFolderName, pattern, Double.toString(worstPatternOccupationPercentage)
            ).toArray(new String[1]));

            if (worstPatternOccupationPercentage < bestOccupationPercentage) {
                bestPattern = pattern;
                bestOccupationPercentage = worstPatternOccupationPercentage;
            }

            System.out.print("\b");
        }

        patternListWriter.close();

        System.out.println("Best reward: " + bestOccupationPercentage);
        System.out.println(bestPattern);

        Date end = new Date();

        System.out.println("Tempo impiegato = " + (end.getTime() - start.getTime()) + " ms");

        System.exit(0);
    }

    /**
     * Questa funzione converte una stringa {@param pattern} in un array di booleani {@return redTrafficLightPattern}
     * dove per ogni step di simulazione su SUMO (il totale � {@param simulationSteps}) il corrispondente booleano �
     * a true se il semaforo � previsto essere rosso per la coda con indice {@param carFlow}.
     * <p>
     * Nella stringa {@param pattern} ogni carattere corrisponde a un secondo di simulazione, poich� la risoluzione
     * temporale (sumo_time_step) della simulazione di SUMO potrebbe essere diversa (spesso 0.1 secondi), la funzione fa
     * anche questa conversione.
     */
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
