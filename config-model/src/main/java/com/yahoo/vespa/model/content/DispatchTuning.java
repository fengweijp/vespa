// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.model.content;

/**
 * Tuning of dispatching to content nodes, see the
 * <a href="https://docs.vespa.ai/documentation/reference/services-content.html#dispatch-tuning">dispatch tuning documentation</a>.
 *
 * @author Simon Thoresen Hult
 */
public class DispatchTuning {

    public static final DispatchTuning empty = new DispatchTuning.Builder().build();

    private final Integer maxHitsPerPartition;
    public enum DispatchPolicy { ROUNDROBIN, ADAPTIVE};
    private final DispatchPolicy dispatchPolicy;
    private final Double minGroupCoverage;
    private final Double minActiveDocsCoverage;

    private DispatchTuning(Builder builder) {
        maxHitsPerPartition = builder.maxHitsPerPartition;
        dispatchPolicy = builder.dispatchPolicy;
        minGroupCoverage = builder.minGroupCoverage;
        minActiveDocsCoverage = builder.minActiveDocsCoverage;
    }

    /** Returns the max number of hits to fetch from each partition, or null to fetch all */
    public Integer maxHitsPerPartition() { return maxHitsPerPartition; }

    /** Returns the policy used to select which group to dispatch a query to */
    public DispatchPolicy dispatchPolicy() { return dispatchPolicy; }

    /** Returns the percentage of nodes in a group which must be up for that group to receive queries */
    public Double minGroupCoverage() { return minGroupCoverage; }

    /** Returns the percentage of documents which must be available in a group for that group to receive queries */
    public Double minActiveDocsCoverage() { return minActiveDocsCoverage; }

    public static class Builder {

        private Integer maxHitsPerPartition;
        private DispatchPolicy dispatchPolicy;
        private Double minGroupCoverage;
        private Double minActiveDocsCoverage;

        public DispatchTuning build() {
            return new DispatchTuning(this);
        }

        public Builder setMaxHitsPerPartition(Integer maxHitsPerPartition) {
            this.maxHitsPerPartition = maxHitsPerPartition;
            return this;
        }
        public Builder setDispatchPolicy(String policy) {
            if (policy != null)
                dispatchPolicy = toDispatchPolicy(policy);
            return this;
        }

        private DispatchPolicy toDispatchPolicy(String policy) {
            switch (policy.toLowerCase()) {
                case "adaptive": case "random": return DispatchPolicy.ADAPTIVE; // TODO: Deprecate 'random' on Java 8
                case "round-robin": return DispatchPolicy.ROUNDROBIN;
                default: throw new IllegalArgumentException("Unknown dispatch policy '" + policy + "'");
            }
        }

        public Builder setMinGroupCoverage(Double minGroupCoverage) {
            this.minGroupCoverage = minGroupCoverage;
            return this;
        }

        public Builder setMinActiveDocsCoverage(Double minCoverage) {
            this.minActiveDocsCoverage = minCoverage;
            return this;
        }
    }

}