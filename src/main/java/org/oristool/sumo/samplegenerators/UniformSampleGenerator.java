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

package org.oristool.sumo.samplegenerators;

import java.math.BigDecimal;

public class UniformSampleGenerator {

    private BigDecimal min;
    private BigDecimal max;

    public UniformSampleGenerator(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    public BigDecimal getSample() {

        if (min.compareTo(max) == 0)
            return min;
        else
            return new BigDecimal(Math.random()).multiply(max.subtract(min)).add(min);

    }
}
