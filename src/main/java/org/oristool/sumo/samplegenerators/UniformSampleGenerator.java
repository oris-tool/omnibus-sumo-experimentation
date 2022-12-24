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
