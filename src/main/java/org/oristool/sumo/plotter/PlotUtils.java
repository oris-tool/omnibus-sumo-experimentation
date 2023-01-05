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

public class PlotUtils {

	public static double[] getLinSpace(double start, double end, double step, boolean includingEnd) {
		int length = (int) ((end - start) / step);
		if (includingEnd)
			length++;
		double[] ret = new double[length];
		ret[0] = start;
		for (int i = 1; i < length; i++) {
			ret[i] = ret[i - 1] + step;
		}
		return ret;
	}

}
