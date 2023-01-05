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

package org.oristool.sumo.sumo;

import org.oristool.sumo.utils.Config;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class SumoConfigurator {

	private static String netConfigFilePath = Config.netConfigFilePath;

	private static String vehConfigFilePath = Config.vehConfigFilePath;

	public static void configureNetwork() throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, TransformerException {

		// imposta massima velocità dei veicoli nelle lane tranviarie
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1InEdge']/lane", "speed",
				Config.maxTramSpeed.toPlainString());
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2InEdge']/lane", "speed",
				Config.maxTramSpeed.toPlainString());
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1OutEdge']/lane", "speed",
				Config.maxTramSpeed.toPlainString());
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2OutEdge']/lane", "speed",
				Config.maxTramSpeed.toPlainString());

		// imposta massima velocità dei veicoli nelle lane riservate alle auto

		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "speed",
				Config.maxVehicleSpeed.toPlainString());
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carOutEdge']/lane", "speed",
				Config.maxVehicleSpeed.toPlainString());

	}

	public static void configureVehicles() throws XPathExpressionException, ParserConfigurationException, SAXException,
			IOException, TransformerException {

		// configure trams
		ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "length",
				Config.tramLength.toString());
		ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "minGap",
				Config.minTramGap.toString());
		ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "maxSpeed",
				Config.maxTramSpeed.toString());

		// configure cars
		ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "length",
				Config.vehicleLength.toString());
		ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "minGap",
				Config.minVehicleGap.toString());
		ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "maxSpeed",
				Config.maxVehicleSpeed.toString());


	}

	public static void changeRoadLenght() throws XPathExpressionException, ParserConfigurationException, SAXException,
			IOException, TransformerException {
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/location", "convBoundary",
		                                              "-100.00,-" + Config.roadLenght.toPlainString() + ",100.00,5.00");
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "length",
		                                              Config.roadLenght.toPlainString());
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "shape",
		                                              "0.00,-" + Config.roadLenght.toPlainString() + " 0.00,-7.20");
		ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/junction[@id='jCarIn']", "shape",
		                                              "-1.60,-" + Config.roadLenght.toPlainString() + " 1.60,-"
				                                              + Config.roadLenght.toPlainString());
	}

}
