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

package org.oristool.sumo.utils;

import au.com.bytecode.opencsv.CSVWriter;
import org.oristool.sumo.plotter.LineToPlot;
import org.oristool.sumo.plotter.PlotUtils;
import org.oristool.sumo.plotter.Plotter;
import org.oristool.sumo.SumoAnalyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class SingleTestLauncher {

    public static void main(String[] args) {
        CSVWriter writer = null;
        if (Config.generateCSV) {
            String fileName =
                    "results/sumo_" + Config.roadLenght + "_" + Config.lambda + "_" + Config.maxVehicleSpeedKmh;
            try {
                writer = new CSVWriter(new FileWriter(fileName + ".csv"), '\t');
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        Config.updateFields();
        executeTest(writer);
        if (Config.generateCSV)
            try {
                Objects.requireNonNull(writer).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void executeTest(CSVWriter writer) {

        System.out.println("Test Launch \n"
                + "roadLength = " + Config.roadLenght + "m \t"
                + "maxQueueSize = " + Config.maxQueueSize + "\t"
                + "maxVehicleSpeed = " + Config.maxVehicleSpeedKmh + "km/h \n"
                + "lambda = " + Config.lambda);

        int hyperPeriod = MathUtils.mcm(Config.periodTime.intValue(),
                Config.twoTram ? Config.periodTime2.intValue() : 1);

        Config.timeBound = BigInteger.valueOf((long) hyperPeriod * Config.analysisHyperperiods);
        Config.cutTimeBound = BigInteger.valueOf((long) hyperPeriod * Config.cutHyperperiods);

        System.out.println("Timebound = "
                + hyperPeriod + " * " + Config.analysisHyperperiods + " = "
                + Config.timeBound + "\n");

        // ANALISI SUMO

        Date start = new Date();

        System.out.println("SUMO simulations: " + Config.sumo_runs + " run" + (Config.sumo_runs > 1 ? "s" : ""));

        int sumoCutStep =
                new BigDecimal(Config.cutTimeBound).divide(Config.sumo_time_step, 0, RoundingMode.FLOOR).intValue();
        double[] sumoCarsUnderSpeedBoundAlongTime;
        double[] sumoAvailability;
        try {
            SumoAnalyzer sumoAnalyzer = new SumoAnalyzer();
            sumoAnalyzer.analyze();
            sumoCarsUnderSpeedBoundAlongTime = sumoAnalyzer.getCarsUnderSpeedBound();
            sumoCarsUnderSpeedBoundAlongTime = Arrays.copyOfRange(sumoCarsUnderSpeedBoundAlongTime, sumoCutStep,
                    sumoCarsUnderSpeedBoundAlongTime.length);
            sumoAvailability = sumoAnalyzer.getAvailability();
            sumoAvailability = Arrays.copyOfRange(sumoAvailability, sumoCutStep, sumoAvailability.length);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Date endSumoAnalysis = new Date();
        long sumoDuration = endSumoAnalysis.getTime() - start.getTime();
        System.out.println("-> duration: " + sumoDuration + " ms");

        // PLOT RESULTS

        String[] strings = new String[sumoCarsUnderSpeedBoundAlongTime.length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Double.toString(sumoAvailability[i]);
        }
        writer.writeNext(strings);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Double.toString(sumoCarsUnderSpeedBoundAlongTime[i]);
        }
        writer.writeNext(strings);

        ArrayList<LineToPlot> linesToPlot = new ArrayList<>();

        double[] sumoLinSpace = PlotUtils.getLinSpace(0, Config.timeBound.subtract(Config.cutTimeBound).intValue(),
                Config.sumo_time_step.doubleValue(), false);

        linesToPlot.add(new LineToPlot("SUMO Average cars detection after " + Config.sumo_runs + " runs", sumoLinSpace,
                sumoCarsUnderSpeedBoundAlongTime));

        linesToPlot.add(new LineToPlot("Sumo Availability", sumoLinSpace, MathUtils.smoothArray(sumoAvailability, 3)));

        Plotter.plot(
                "sumo_" + Config.roadLenght + "_" + Config.lambda + "_" + Config.maxVehicleSpeedKmh,
                "s", "cars", linesToPlot, Config.saveAsPNG, null, Config.plotResults);

    }
}
