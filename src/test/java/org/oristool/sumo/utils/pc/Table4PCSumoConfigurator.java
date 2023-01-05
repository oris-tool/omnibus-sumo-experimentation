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

package org.oristool.sumo.utils.pc;

import org.oristool.sumo.Table5PatternComparator;
import org.oristool.sumo.sumo.ChangeXmlConfiguration;
import org.oristool.sumo.utils.ScenarioDefiner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class Table4PCSumoConfigurator {

    private static String netConfigFilePath = Table5PatternComparator.netConfigFilePath;

    private static String vehConfigFilePath = Table5PatternComparator.vehConfigFilePath;

    public static void configureNetwork(int flowIndex) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, TransformerException {

        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1InEdge']/lane", "speed",
                Table5PatternComparator.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2InEdge']/lane", "speed",
                Table5PatternComparator.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1OutEdge']/lane", "speed",
                Table5PatternComparator.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2OutEdge']/lane", "speed",
                Table5PatternComparator.maxTramSpeed.toPlainString());

        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "speed",
                ScenarioDefiner.maxVehicleSpeeds.get(flowIndex).toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carOutEdge']/lane", "speed",
                ScenarioDefiner.maxVehicleSpeeds.get(flowIndex).toPlainString());

    }

    public static void configureVehicles(int flowIndex) throws XPathExpressionException, ParserConfigurationException, SAXException,
            IOException, TransformerException {

        // configure trams
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "length",
                Table5PatternComparator.tramLength.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "minGap",
                Table5PatternComparator.minTramGap.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "maxSpeed",
                Table5PatternComparator.maxTramSpeed.toString());

        // configure cars
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "length",
                Table5PatternComparator.vehicleLength.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "minGap",
                Table5PatternComparator.minVehicleGap.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "maxSpeed",
                ScenarioDefiner.maxVehicleSpeeds.get(flowIndex).toString());
    }

    public static void changeRoadLenght(int flowIndex) throws XPathExpressionException, ParserConfigurationException,
            SAXException,
            IOException, TransformerException {
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/location", "convBoundary",
                "-100.00,-" + ScenarioDefiner.roadLenghts.get(flowIndex).toPlainString() + ",100.00,5.00");
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "length",
                ScenarioDefiner.roadLenghts.get(flowIndex).toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "shape",
                "0.00,-" + ScenarioDefiner.roadLenghts.get(flowIndex).toPlainString() + " 0.00,-7.20");
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/junction[@id='jCarIn']", "shape",
                "-1.60,-" + ScenarioDefiner.roadLenghts.get(flowIndex).toPlainString() + " 1.60,-"
                        + ScenarioDefiner.roadLenghts.get(flowIndex).toPlainString());
    }

}
