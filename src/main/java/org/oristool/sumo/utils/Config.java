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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Classe di configurazione per una simulazione.
 */
public class Config {

    public static final BigDecimal crossroadLenght = new BigDecimal("12.20"); // unmodifiable
    public static boolean plotResults = false;
    public static boolean saveAsPNG = true;
    public static boolean generateCSV = true;

    // Analysis & Results
    public static boolean saveData = false;
    public static BigDecimal timeStep = new BigDecimal("0.1");
    public static int analysisHyperperiods = 5;
    public static BigInteger timeBound = new BigInteger("1100");
    public static int cutHyperperiods = 2;
    public static BigInteger cutTimeBound = new BigInteger("0");

    // Variable params
    public static BigDecimal roadLenght = new BigDecimal("150");
    public static BigDecimal lambda = new BigDecimal("0.9");

    // Sumo Params
    public static String SUMO_EXE = "sumo.exe"; // -gui for interface
    public static String WIN_SUMO_BIN = System.getenv("SUMO_HOME") + "\\bin\\" + SUMO_EXE;
    public static String CONFIG_FILE = "simulation\\TramWay\\SumoEnvironment\\configuration.sumo.cfg";
    public static String mainPath = "simulation\\TramWay\\";
    public static final String standardVehConfigFilePath = mainPath + "SumoEnvironment\\vehicles.xml";
    public static String vehConfigFilePath = standardVehConfigFilePath; // XXX agire qui per tempo di reazione
    public static final String noTauVehConfigFilePath = mainPath + "SumoEnvironment\\vehicles_with_no_tau.xml";
    public static String netConfigFilePath = mainPath + "SumoEnvironment\\network.net.xml";
    public static String sensorConfigFilePath = mainPath + "SumoEnvironment\\loops.xml";
    public static int sumo_runs = 1;
    public static BigDecimal sumo_time_step = new BigDecimal("0.1");
    public static boolean abortInsertWhenQueueFull = true;
    public static boolean shiftedExp = true;
    public static BigDecimal shift = new BigDecimal("0.66");

    public static BigDecimal maxVehicleSpeedKmh = new BigDecimal("50");
    public static BigDecimal maxTramSpeed = new BigDecimal("30");
    public static BigDecimal minTramGap = new BigDecimal("10");
    public static BigDecimal tramLength = new BigDecimal("20");
    public static BigDecimal maxVehicleSpeed = maxVehicleSpeedKmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR);
    public static BigDecimal maxSpeedOnQueue = maxVehicleSpeed.add(new BigDecimal("10"));
    public static BigDecimal minVehicleGap = new BigDecimal("0.3");

    // Queue Params
    public static BigDecimal vehicleLength = new BigDecimal("4.5");
    public static BigDecimal upperBoundSpeedOnQueue = maxVehicleSpeedKmh.add(BigDecimal.ONE);
    public static BigInteger maxQueueSize =
            roadLenght.divide(vehicleLength.add(minVehicleGap), 0, RoundingMode.FLOOR).toBigIntegerExact();

    public static BigInteger initialElements = new BigInteger("0");

    // First Tram Params

    public static BigInteger periodTime = new BigInteger("220");
    public static BigInteger phaseTime = new BigInteger("0");
    public static BigInteger delayEFTime = new BigInteger("0");
    public static BigInteger delayLFTime = new BigInteger("120");
    public static BigInteger crosslightAntTime = new BigInteger("5");
    public static BigInteger leavingEFTime = new BigInteger("6");
    public static BigInteger leavingLFTime = new BigInteger("14");

    // Second Tram Params

    public static boolean twoTram = true;

    public static BigInteger periodTime2 = new BigInteger("220");
    public static BigInteger phaseTime2 = new BigInteger("40");
    public static BigInteger delayEFTime2 = new BigInteger("0");
    public static BigInteger delayLFTime2 = new BigInteger("40");
    public static BigInteger crosslightAntTime2 = new BigInteger("5");
    public static BigInteger leavingEFTime2 = new BigInteger("6");
    public static BigInteger leavingLFTime2 = new BigInteger("14");

    public static void updateFields() {
        maxVehicleSpeed = maxVehicleSpeedKmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR);
        maxSpeedOnQueue = maxVehicleSpeed.add(new BigDecimal("10"));
        maxQueueSize =
                roadLenght.divide(vehicleLength.add(minVehicleGap), 0, RoundingMode.FLOOR).toBigIntegerExact();

        upperBoundSpeedOnQueue = maxVehicleSpeedKmh.add(BigDecimal.ONE);
//		upperBoundSpeedOnQueue = maxVehicleSpeedKmh.divide(BigDecimal.valueOf(3),0,RoundingMode.FLOOR);
    }

}
