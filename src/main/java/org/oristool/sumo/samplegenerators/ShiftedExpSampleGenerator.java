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

public final class ShiftedExpSampleGenerator {

    private final BigDecimal rate;
    private final BigDecimal shift;

    public ShiftedExpSampleGenerator(BigDecimal rate, BigDecimal shift) {
        this.rate = rate;
        this.shift = shift;
    }

    public BigDecimal getSample() {

        return new BigDecimal(-Math.log(1 - Math.random()) / rate.doubleValue()).add(shift);
    }
}
