package org.oristool.sumo.utils;

import org.oristool.sumo.VaryingParamsSolutionPlotter;
import org.oristool.sumo.sumo.ChangeXmlConfiguration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class VaryingParamsSumoConfigurator {

    private static String netConfigFilePath = VaryingParamsSolutionPlotter.netConfigFilePath;

    private static String vehConfigFilePath = VaryingParamsSolutionPlotter.vehConfigFilePath;

    public static void configureNetwork(int flowIndex) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, TransformerException {

        // imposta massima velocità dei veicoli nelle lane tranviarie
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1InEdge']/lane", "speed",
                VaryingParamsSolutionPlotter.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2InEdge']/lane", "speed",
                VaryingParamsSolutionPlotter.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1OutEdge']/lane", "speed",
                VaryingParamsSolutionPlotter.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2OutEdge']/lane", "speed",
                VaryingParamsSolutionPlotter.maxTramSpeed.toPlainString());

        // imposta massima velocità dei veicoli nelle lane riservate alle auto

        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "speed",
                VaryingParamsSolutionPlotter.maxVehicleSpeeds.get(flowIndex).toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carOutEdge']/lane", "speed",
                VaryingParamsSolutionPlotter.maxVehicleSpeeds.get(flowIndex).toPlainString());

    }

    public static void configureVehicles(int flowIndex) throws XPathExpressionException, ParserConfigurationException, SAXException,
            IOException, TransformerException {

        // configure trams
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "length",
                VaryingParamsSolutionPlotter.tramLength.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "minGap",
                VaryingParamsSolutionPlotter.minTramGap.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "maxSpeed",
                VaryingParamsSolutionPlotter.maxTramSpeed.toString());

        // configure cars
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "length",
                VaryingParamsSolutionPlotter.vehicleLength.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "minGap",
                VaryingParamsSolutionPlotter.minVehicleGap.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "maxSpeed",
                VaryingParamsSolutionPlotter.maxVehicleSpeeds.get(flowIndex).toString());


    }

    public static void changeRoadLenght(int flowIndex) throws XPathExpressionException, ParserConfigurationException,
            SAXException,
            IOException, TransformerException {
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/location", "convBoundary",
                "-100.00,-" + VaryingParamsSolutionPlotter.roadLenghts.get(flowIndex).toPlainString() + ",100.00,5.00");
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "length",
                VaryingParamsSolutionPlotter.roadLenghts.get(flowIndex).toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "shape",
                "0.00,-" + VaryingParamsSolutionPlotter.roadLenghts.get(flowIndex).toPlainString() + " 0.00,-7.20");
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/junction[@id='jCarIn']", "shape",
                "-1.60,-" + VaryingParamsSolutionPlotter.roadLenghts.get(flowIndex).toPlainString() + " 1.60,-"
                        + VaryingParamsSolutionPlotter.roadLenghts.get(flowIndex).toPlainString());
    }

}
