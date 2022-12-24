package org.oristool.sumo.utils.pattern;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SemaphorePattern {

    public static final String RED_SYMBOL = "9";

    private List<VehicleFlow> involvedFlows;
    private List<VehicleFlow> remainingFlows;
    private int period;
    private int redTime;
    private VehicleFlow lastFlowServed;

    private boolean empty;
    private String firstFlowId;
    private String lastFlowId;

    private StringBuilder scheduleBuilder;

    private int lastSlotTimeStep;

    public SemaphorePattern(List<VehicleFlow> flows, int period, int redTime) {
        this.involvedFlows = flows;
        this.remainingFlows = flows;
        this.period = period;
        this.redTime = redTime;
        this.lastSlotTimeStep = 0;
        this.scheduleBuilder = new StringBuilder();
        this.lastFlowServed = null;
        this.empty = true;
    }

    public SemaphorePattern(SemaphorePattern subPattern) {
        this.involvedFlows = subPattern.getInvolvedFlows();
        this.remainingFlows = subPattern.getRemainingFlows();
        this.period = subPattern.getPeriod();
        this.redTime = subPattern.getRedTime();
        this.lastSlotTimeStep = subPattern.getLastSlotTimeStep();
        this.scheduleBuilder = new StringBuilder(subPattern.getScheduleBuilder());
        this.lastFlowServed = subPattern.getLastFlowServed();
        this.empty = subPattern.isEmpty();
        this.firstFlowId = subPattern.getFirstFlowId();
        this.lastFlowId = subPattern.getLastFlowId();

    }

    public String getSchedule() {
        return scheduleBuilder.toString();
    }

    public void addGreenSlot(VehicleFlow flow, int greenSlot) {
        if (this.isEmpty()) {
            this.firstFlowId = flow.getId();
            this.empty = false;
        }
        this.lastFlowId = flow.getId();

        scheduleBuilder.append(flow.getId().repeat(greenSlot));
        scheduleBuilder.append(RED_SYMBOL.repeat(redTime));

        if (remainingFlows.contains(flow))
            remainingFlows.remove(flow);

        lastFlowServed = flow.getClone();
        lastSlotTimeStep += greenSlot + redTime;
    }

    public boolean isRemainingPatternFeasible() {
        int minimumTotalGreenTime = 0;
        for (VehicleFlow flow : getRemainingFlows()) {
            minimumTotalGreenTime += flow.getMinimumGreenSlot();
        }
        int minumumTotalRedTime = getRedTime() * getInvolvedFlows().size();
        return minimumTotalGreenTime + minumumTotalRedTime <= getRemainingTime();
    }

    public boolean firstAndLastFlowsCoincide() {
        if (firstFlowId != null && lastFlowId != null)
            return this.getFirstFlowId().equals(getLastFlowId());
        return false;
    }

    public boolean noMorePossibleSlotsExist() {
        int minimumSlot = involvedFlows.stream().min(Comparator.comparing(VehicleFlow::getMinimumGreenSlot)).get()
                .getMinimumGreenSlot();
        return minimumSlot + redTime > getRemainingTime();
    }

    public void fillRemainingWithRed() {
        scheduleBuilder.append(RED_SYMBOL.repeat(getRemainingTime()));
    }

    public void fillRemainingGreenTimeWithLastFlow() {
        int greenTime = getRemainingTime();
        if (greenTime > 0) {
            scheduleBuilder.delete(lastSlotTimeStep - redTime, lastSlotTimeStep);
            scheduleBuilder.append(lastFlowId.repeat(greenTime));
            scheduleBuilder.append(RED_SYMBOL.repeat(redTime));
        }
        lastSlotTimeStep += greenTime + redTime;
    }

    public boolean representsAllFlowsAtLeastOnce() {
        return remainingFlows.isEmpty();
    }

    public int getRemainingTime() {
        return period - lastSlotTimeStep;
    }

    public boolean hasServedThisFlowPreviously(VehicleFlow flow) {
        if (lastFlowServed == null)
            return false;
        return flow.equals(lastFlowServed);
    }

    public List<VehicleFlow> getInvolvedFlows() {
        return involvedFlows.stream().map(VehicleFlow::getClone).collect(Collectors.toList());
    }

    public List<VehicleFlow> getRemainingFlows() {
        return remainingFlows.stream().map(VehicleFlow::getClone).collect(Collectors.toList());
    }

    public VehicleFlow getLastFlowServed() {
        if (lastFlowServed == null)
            return null;
        return lastFlowServed.getClone();
    }

    public int getPeriod() {
        return period;
    }

    public int getRedTime() {
        return redTime;
    }

    public int getLastSlotTimeStep() {
        return lastSlotTimeStep;
    }

    public StringBuilder getScheduleBuilder() {
        return scheduleBuilder;
    }

    public String getFirstFlowId() {
        return firstFlowId;
    }

    public String getLastFlowId() {
        return lastFlowId;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.scheduleBuilder.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SemaphorePattern other = (SemaphorePattern) obj;
        return this.scheduleBuilder.toString().equals(other.getSchedule());
    }
}
