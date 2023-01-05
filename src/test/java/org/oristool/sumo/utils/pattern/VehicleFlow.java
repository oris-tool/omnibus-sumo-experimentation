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

package org.oristool.sumo.utils.pattern;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class VehicleFlow {

	@Override
	public int hashCode() {
		return Objects.hash(greenSlots, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleFlow other = (VehicleFlow) obj;
		return Objects.equals(greenSlots, other.greenSlots) && Objects.equals(id, other.id);
	}

	private String id;
	private List<Integer> greenSlots;

	public VehicleFlow(String id, List<Integer> greenSlots) {
		this.id = id;
		this.greenSlots = greenSlots;
	}
	
	public VehicleFlow getClone() {
		return new VehicleFlow(id, greenSlots);
	}

	public List<Integer> getGreenSlots() {
		return greenSlots;
	}

	public String getId() {
		return id;
	}

	public int getMinimumGreenSlot() {
		return greenSlots.stream().min(Comparator.comparing(Integer::valueOf)).get();
	}

}
