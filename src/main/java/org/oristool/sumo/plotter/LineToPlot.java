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

package org.oristool.sumo.plotter;

public class LineToPlot {

	private String seriesName;
	private double[] xValues;
	private double[] yValues;
	
	public LineToPlot(String seriesName, double[] xValues, double[] yValues) {
		if(xValues.length!=yValues.length)
			throw new IllegalArgumentException("Can't be plotted.");
		
		this.seriesName = seriesName;
		this.xValues = new double[xValues.length];
		this.yValues = new double[yValues.length];
		for(int i=0; i<xValues.length; i++) {
			this.xValues[i] = xValues[i];
			this.yValues[i] = yValues[i];
		}
	}
	
	public String getSeriesName() {
		return seriesName;
	}

	public double[] getxValues() {
		return xValues;
	}

	public double[] getyValues() {
		return yValues;
	}

}
