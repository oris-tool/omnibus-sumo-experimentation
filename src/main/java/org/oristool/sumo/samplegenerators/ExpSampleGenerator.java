package org.oristool.sumo.samplegenerators;

import java.math.BigDecimal;

public final class ExpSampleGenerator {

    private final BigDecimal rate;

    public ExpSampleGenerator(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getSample() {

        return new BigDecimal(-Math.log(1 - Math.random()) / rate.doubleValue());
    }
}
