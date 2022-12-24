package org.oristool.sumo.utils.pc;

import org.oristool.sumo.Table2PatternComparator;
import org.oristool.sumo.sumo.ChangeXmlConfiguration;
import org.oristool.sumo.utils.ScenarioDefiner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class Table2PCSumoConfigurator {

    private static String netConfigFilePath = Table2PatternComparator.netConfigFilePath;

    private static String vehConfigFilePath = Table2PatternComparator.vehConfigFilePath;

    public static void configureNetwork(int flowIndex) throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, TransformerException {

        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1InEdge']/lane", "speed",
                Table2PatternComparator.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2InEdge']/lane", "speed",
                Table2PatternComparator.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram1OutEdge']/lane", "speed",
                Table2PatternComparator.maxTramSpeed.toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='tram2OutEdge']/lane", "speed",
                Table2PatternComparator.maxTramSpeed.toPlainString());

        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carInEdge']/lane", "speed",
                ScenarioDefiner.maxVehicleSpeeds.get(flowIndex).toPlainString());
        ChangeXmlConfiguration.changeXmlConfiguration(netConfigFilePath, "/net/edge[@id='carOutEdge']/lane", "speed",
                ScenarioDefiner.maxVehicleSpeeds.get(flowIndex).toPlainString());

    }

    public static void configureVehicles(int flowIndex) throws XPathExpressionException, ParserConfigurationException, SAXException,
            IOException, TransformerException {

        // configure trams
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "length",
                Table2PatternComparator.tramLength.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "minGap",
                Table2PatternComparator.minTramGap.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='tramway']", "maxSpeed",
                Table2PatternComparator.maxTramSpeed.toString());

        // configure cars
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "length",
                Table2PatternComparator.vehicleLength.toString());
        ChangeXmlConfiguration.changeXmlConfiguration(vehConfigFilePath, "additional/vType[@id='car']", "minGap",
                Table2PatternComparator.minVehicleGap.toString());
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
