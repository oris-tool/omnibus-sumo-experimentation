package org.oristool.sumo;

import de.tudresden.sumo.cmd.*;
import de.tudresden.ws.container.SumoStringList;
import de.tudresden.ws.container.SumoVehicleData;
import it.polito.appeal.traci.SumoTraciConnection;
import org.oristool.sumo.samplegenerators.ExpSampleGenerator;
import org.oristool.sumo.samplegenerators.ShiftedExpSampleGenerator;
import org.oristool.sumo.samplegenerators.UniformSampleGenerator;
import org.oristool.sumo.sumo.SumoConfigurator;
import org.oristool.sumo.utils.Config;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SumoAnalyzer {

	private double[] availability;
	private double[] carsUnderSpeedBound;
	private List<Integer> expectedCarsPerRun;
	private List<Integer> abortedCarsPerRun;
	private double[] abortedCars;
	private List<Integer> detectedCarsPerRun;

	public SumoAnalyzer analyze() {

		double INV_STEP_LENGTH = Math.pow(Config.sumo_time_step.doubleValue(), -1);
		int SIMULATION_STEPS = (int) (Config.timeBound.doubleValue() * INV_STEP_LENGTH);

		this.availability = new double[SIMULATION_STEPS];
		this.carsUnderSpeedBound = new double[SIMULATION_STEPS];
		this.expectedCarsPerRun = new ArrayList<>();
		this.abortedCarsPerRun = new ArrayList<>();
		this.abortedCars = new double[SIMULATION_STEPS];
		this.detectedCarsPerRun = new ArrayList<>();

		for (int r = 0; r < Config.sumo_runs; r++) {

			System.out.print((r + 1) + " ");

			try {
				configureEnvironment();
			} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException
					| TransformerException e) {
				e.printStackTrace();
			}

			/*
			 * Tempi di arrivo del primo tram.
			 */
			int EXPECTED_TRAMS_1 = Config.timeBound.divide(Config.periodTime).intValue() + 2;
			double[] arrivalTimes = new double[EXPECTED_TRAMS_1];
			BigDecimal t1_period_step = new BigDecimal(Config.periodTime).divide(Config.sumo_time_step,
			                                                                     RoundingMode.HALF_UP);
			BigDecimal t1_phase_step = new BigDecimal(Config.phaseTime).divide(Config.sumo_time_step,
			                                                                   RoundingMode.HALF_UP);
			BigDecimal t1_delayEFT_step = new BigDecimal(Config.delayEFTime).divide(Config.sumo_time_step,
			                                                                        RoundingMode.HALF_UP);
			BigDecimal t1_delayLFT_step = new BigDecimal(Config.delayLFTime).divide(Config.sumo_time_step,
			                                                                        RoundingMode.HALF_UP);
			for (int i = 0; i < arrivalTimes.length; i++) {
				double arrival_start = Double.parseDouble(Integer.toString(i)) * t1_period_step.doubleValue()
						+ t1_phase_step.doubleValue();
				arrivalTimes[i] =
						new UniformSampleGenerator(BigDecimal.valueOf(arrival_start + t1_delayEFT_step.doubleValue()),
						                   BigDecimal.valueOf(arrival_start + t1_delayLFT_step.doubleValue()))
								.getSample().doubleValue();
			}

			/*
			 * Tempi di arrivo del secondo tram.
			 */
			int EXPECTED_TRAMS_2 = Config.timeBound.divide(Config.periodTime2).intValue() + 2;
			double[] arrivalTimes2 = new double[EXPECTED_TRAMS_2];
			BigDecimal t2_period_step = new BigDecimal(Config.periodTime2).divide(Config.sumo_time_step,
			                                                                      RoundingMode.HALF_UP);
			BigDecimal t2_phase_step = new BigDecimal(Config.phaseTime2).divide(Config.sumo_time_step,
			                                                                    RoundingMode.HALF_UP);
			BigDecimal t2_delayEFT_step = new BigDecimal(Config.delayEFTime2).divide(Config.sumo_time_step,
			                                                                         RoundingMode.HALF_UP);
			BigDecimal t2_delayLFT_step = new BigDecimal(Config.delayLFTime2).divide(Config.sumo_time_step,
			                                                                         RoundingMode.HALF_UP);
			for (int i = 0; i < arrivalTimes2.length; i++) {
				double arrival_start = Double.parseDouble(Integer.toString(i)) * t2_period_step.doubleValue()
						+ t2_phase_step.doubleValue();
				arrivalTimes2[i] =
						new UniformSampleGenerator(BigDecimal.valueOf(arrival_start + t2_delayEFT_step.doubleValue()),
						                   BigDecimal.valueOf(arrival_start + t2_delayLFT_step.doubleValue()))
								.getSample().doubleValue();
			}

			/*
			 * Tempi di arrivo per le auto.
			 */
			double lastCarTime = 0.;
			ArrayList<Integer> carArrivalSteps = new ArrayList<>();
			if (Config.shiftedExp) {
				final BigDecimal newRate =
						BigDecimal.ONE.divide(BigDecimal.ONE.divide(Config.lambda, RoundingMode.HALF_UP).subtract(Config.shift), RoundingMode.HALF_UP);
				while (lastCarTime <= Config.timeBound.doubleValue()) {
					double sample =
							new ShiftedExpSampleGenerator(newRate, Config.shift).getSample().doubleValue();
					lastCarTime += sample;
					carArrivalSteps.add((int) (lastCarTime * INV_STEP_LENGTH));
				}
			} else {
				while (lastCarTime <= Config.timeBound.doubleValue()) {
					double sample =
							new ExpSampleGenerator(Config.lambda).getSample().doubleValue();
					lastCarTime += sample;
					carArrivalSteps.add((int) (lastCarTime * INV_STEP_LENGTH));
				}
			}

			// Start simulation
			SumoTraciConnection conn = new SumoTraciConnection(Config.WIN_SUMO_BIN, Config.CONFIG_FILE);
			conn.addOption("step-length", Config.sumo_time_step.toString());
			conn.addOption("start", "0"); // Start Sumo immediately.
			try {
				conn.runServer();
			} catch (IOException e) {
				e.printStackTrace();
			}

			int percent = 0;
			// Mette il semaforo a verde per le auto.
			try {
				conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 1));
			} catch (Exception e) {
				e.printStackTrace();
			}

			int frstTramCounter = 0;
			int scndTramCounter = 0;
			int carCounter = 0;
			int abortedCarCounter = 0;
			HashSet<String> detectedCars = new HashSet<>();

			for (int ts = 0; ts < SIMULATION_STEPS; ts++) {

				//				// Print percentage
				//				if ((ts * 100) / SIMULATION_STEPS >= percent) {
				//					percent++;
				//					if (percent % 10 == 0)
				//						System.out.print(percent + "% ");
				//					if (percent == 100)
				//						System.out.println();
				//				}

				// Avanza la simulazione di uno step temporale
				try {
					conn.do_timestep();
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * Immette nella rete un'auto o un tram se lo step attuale corrisponde ad uno di
				 * quelli previsti per l'ingresso di un veicolo.
				 */

				if (ts >= carArrivalSteps.get(carCounter)) {
					SumoVehicleData sensorCarInStart;
					try {
						sensorCarInStart = (SumoVehicleData) conn
								.do_job_get(Inductionloop.getVehicleData("sensorCarInStart"));
						if (!Config.abortInsertWhenQueueFull || sensorCarInStart == null || sensorCarInStart.ll.size() == 0) {
							conn.do_job_set(Vehicle.addFull("CarID_" + carCounter, "carRoute", "car", "now", "0", "0",
							                                "max", "current", "max", "current", "", "", "", 0, 0));
						} else {
							abortedCarCounter++;
							this.abortedCars[ts] += (1. / (double) Config.sumo_runs);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					carCounter++;
				}

				if (ts >= arrivalTimes[frstTramCounter]) {
					try {
						conn.do_job_set(Vehicle.addFull("TramID_" + frstTramCounter, "tram1Route", "tramway", "now",
						                                "0",
						                                "0",
						                                new BigDecimal("100")
								                                .divide(new BigDecimal(Config.crosslightAntTime),
								                                        RoundingMode.HALF_UP)
								                                .toPlainString(), "current", "max", "current", "", "",
						                                "", 0, 0));
					} catch (Exception e) {
						e.printStackTrace();
					}
					frstTramCounter++;
				}

				if (ts >= arrivalTimes2[scndTramCounter]) {
					try {
						conn.do_job_set(Vehicle.addFull("Tram2ID_" + scndTramCounter, "tram2Route", "tramway", "now",
						                                "0",
						                                "0",
						                                new BigDecimal("100")
								                                .divide(new BigDecimal(Config.crosslightAntTime2),
								                                        RoundingMode.HALF_UP)
								                                .toPlainString(), "current", "max", "current", "", "",
						                                "", 0, 0));
					} catch (Exception e) {
						e.printStackTrace();
					}
					scndTramCounter++;
				}

				/*
				 * Se ci sono tram su ogni linea, imposta il semaforo a rosso.
				 */

				SumoStringList tram1InEdge_0_list = null;
				try {
					tram1InEdge_0_list = (SumoStringList) conn
							.do_job_get(Lane.getLastStepVehicleIDs("tram1InEdge_0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (tram1InEdge_0_list != null && tram1InEdge_0_list.size() > 0)
					try {
						conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 0));
					} catch (Exception e) {
						e.printStackTrace();
					}

				SumoStringList tram2InEdge_0_list = null;
				try {
					tram2InEdge_0_list = (SumoStringList) conn
							.do_job_get(Lane.getLastStepVehicleIDs("tram2InEdge_0"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (tram2InEdge_0_list != null && tram2InEdge_0_list.size() > 0)
					try {
						conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 0));
					} catch (Exception e) {
						e.printStackTrace();
					}

				/*
				 * Se c'è un tram sul sensore all'incrocio, ne imposta il tempo di
				 * attraversamento. in realtà sembra impostare la durata del
				 * semaforo, ma forse va bene anche così
				 */

				SumoVehicleData sensorTram1Crossroad = null;
				try {
					sensorTram1Crossroad = (SumoVehicleData) conn
							.do_job_get(Inductionloop.getVehicleData("sensorTram1Crossroad"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (sensorTram1Crossroad != null && sensorTram1Crossroad.ll != null
						&& sensorTram1Crossroad.ll.size() > 0) {
					try {
						conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 0));
						conn.do_job_set(
								Trafficlight.setPhaseDuration("crossRoadTL",
								                              new UniformSampleGenerator(new BigDecimal(Config.leavingEFTime),
								                                                 new BigDecimal(Config.leavingLFTime))
										                              .getSample().setScale(2,
										                                                    RoundingMode.HALF_DOWN).doubleValue()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				SumoVehicleData sensorTram2Crossroad = null;
				try {
					sensorTram2Crossroad = (SumoVehicleData) conn
							.do_job_get(Inductionloop.getVehicleData("sensorTram2Crossroad"));
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (sensorTram2Crossroad != null && sensorTram2Crossroad.ll != null
						&& sensorTram2Crossroad.ll.size() > 0) {
					try {
						conn.do_job_set(Trafficlight.setPhase("crossRoadTL", 0));
						conn.do_job_set(
								Trafficlight.setPhaseDuration("crossRoadTL",
								                              new UniformSampleGenerator(new BigDecimal(Config.leavingEFTime2),
								                                                 new BigDecimal(Config.leavingLFTime2))
										                              .getSample().setScale(2,
										                                                    RoundingMode.HALF_DOWN).doubleValue()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				/*
				 * Controllo se l'auto è stata immessa
				 */

				SumoVehicleData sensorCarInStart;
				try {
					sensorCarInStart = (SumoVehicleData) conn
							.do_job_get(Inductionloop.getVehicleData("sensorCarInStart"));
					if (sensorCarInStart != null && sensorCarInStart.ll.size() > 0) {
						for (SumoVehicleData.VehicleData c : sensorCarInStart.ll) {
							detectedCars.add(c.vehID);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * Verifica dello stato del semaforo per la misurazione dell'availability
				 */

				String tlPhase = null;
				try {
					tlPhase = (String) conn.do_job_get(Trafficlight.getRedYellowGreenState("crossRoadTL"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if ("rGr".equals(tlPhase)) {
					this.availability[ts] += (1. / (double) Config.sumo_runs);
				}

				/*
				 * Conteggio delle auto in coda ad ogni step.
				 */

				SumoStringList queueList = null;
				try {
					queueList = (SumoStringList) conn.do_job_get(Edge.getLastStepVehicleIDs("carInEdge"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				BigInteger carCounters = BigInteger.ZERO;
				assert queueList != null;
				for (String vehId : queueList) {
					double speed = 0;
					try {
						speed = (double) conn.do_job_get(Vehicle.getSpeed(vehId));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (speed < Config.upperBoundSpeedOnQueue.divide(new BigDecimal("3.6"), 3, RoundingMode.HALF_DOWN).doubleValue()) {
						carCounters = carCounters.add(BigInteger.ONE);
					}
				}

				this.carsUnderSpeedBound[ts] += carCounters.doubleValue() / (double) Config.sumo_runs;

			}
			conn.close();

			expectedCarsPerRun.add(carArrivalSteps.size() - 1);
			abortedCarsPerRun.add(abortedCarCounter);
			detectedCarsPerRun.add(detectedCars.size());

			try {
				Runtime.getRuntime().exec("taskkill /F /IM " + Config.SUMO_EXE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println();

		return this;
	}

	private void configureEnvironment() throws XPathExpressionException, ParserConfigurationException,
			SAXException, IOException, TransformerException {
		SumoConfigurator.changeRoadLenght();
		SumoConfigurator.configureNetwork();
		SumoConfigurator.configureVehicles();
	}

	public double[] getCarsUnderSpeedBound() {
		return this.carsUnderSpeedBound;
	}

	public double[] getAvailability() {
		return this.availability;
	}

	public double getExpectedCarsMean() {
		return this.expectedCarsPerRun.stream().reduce(0, Integer::sum).doubleValue() / (double) this.expectedCarsPerRun.size();
	}

	public double getDetectedCarsMean() {
		return this.detectedCarsPerRun.stream().reduce(0, Integer::sum).doubleValue() / (double) this.detectedCarsPerRun.size();
	}

	public double getAbortedCarsMean() {
		return this.abortedCarsPerRun.stream().reduce(0, Integer::sum).doubleValue() / (double) this.abortedCarsPerRun.size();
	}

	public double[] getAbortedCars() {
		return this.abortedCars;
	}
}
