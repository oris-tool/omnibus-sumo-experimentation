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
