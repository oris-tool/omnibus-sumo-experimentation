package org.oristool.sumo.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScenarioDefiner {

    private static final BigDecimal crossroadLenght = new BigDecimal("12.20"); // unmodifiable
    private static BigDecimal carSpace = new BigDecimal("4.8");

    /*
        Info tram 1
     */
    private static String t1_name = "bin1";
    public static BigInteger t1_periodTime = BigInteger.valueOf(220);
    public static BigInteger t1_phaseTime = BigInteger.ZERO;
    public static BigInteger t1_delayEFTime = BigInteger.ZERO;
    public static BigInteger t1_delayLFTime = BigInteger.valueOf(120);
    public static BigInteger t1_crosslightAntTime = BigInteger.valueOf(5);
    public static BigInteger t1_leavingEFTime = BigInteger.valueOf(6);
    public static BigInteger t1_leavingLFTime = BigInteger.valueOf(14);

    /*
        Info tram 2
     */
    public static String t2_name = "bin2";
    public static BigInteger t2_periodTime = BigInteger.valueOf(220);
    public static BigInteger t2_phaseTime = BigInteger.valueOf(40);
    public static BigInteger t2_delayEFTime = BigInteger.ZERO;
    public static BigInteger t2_delayLFTime = BigInteger.valueOf(40);
    public static BigInteger t2_crosslightAntTime = BigInteger.valueOf(5);
    public static BigInteger t2_leavingEFTime = BigInteger.valueOf(6);
    public static BigInteger t2_leavingLFTime = BigInteger.valueOf(14);

    public static int carFlows = 3;

    public static List<BigDecimal> arrivalRates = Arrays.asList(
            BigDecimal.valueOf(0.05),
            BigDecimal.valueOf(0.1),
            BigDecimal.valueOf(0.15)
    );

    public static List<BigDecimal> maxVehicleSpeedsKmh = Arrays.asList(
            new BigDecimal("50"),
            new BigDecimal("50"),
            new BigDecimal("50")
    );

    public static List<BigDecimal> roadLenghts = Arrays.asList(
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(150),
            BigDecimal.valueOf(150)
    );

    public static List<BigInteger> maxQueueSizes =
            roadLenghts.stream().map(rl ->
                    rl.divide(carSpace, 0, RoundingMode.FLOOR).toBigInteger()
            ).collect(Collectors.toList());

    public static List<BigDecimal> maxVehicleSpeeds =
            maxVehicleSpeedsKmh.stream().map(skmh ->
                    skmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR)
            ).collect(Collectors.toList());

    public static BigDecimal timeStep = BigDecimal.valueOf(0.1);
    public static BigInteger semPeriod = new BigInteger("110");

    public static void updateFields() {
        maxQueueSizes =
                roadLenghts.stream().map(rl ->
                        rl.divide(carSpace, 0, RoundingMode.FLOOR).toBigInteger()
                ).collect(Collectors.toList());

        maxVehicleSpeeds =
                maxVehicleSpeedsKmh.stream().map(skmh ->
                        skmh.divide(new BigDecimal("3.6"), 3, RoundingMode.FLOOR)
                ).collect(Collectors.toList());
    }
}
