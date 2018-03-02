package com.eaybars.benchmark;

import java.io.IOException;
import java.io.Serializable;

public class Transaction {
    private static final String CURRENT_OPTIONS_NAME = "com.eaybars.benchmark.graph.transaction.currentoptions";

    private Options options = new Options();

    public Transaction withCommitInterval(int commitInterval) {
        this.options.commitInterval = commitInterval;
        return this;
    }

    public Transaction reinitialisePeriod(int period) {
        options.graphReinitialisationPeriod = period;
        return this;
    }

    public static Transaction.Options currentOptions() {
        try {
            return Information.TEMPORARY.load(Transaction.Options.class, System.getProperty(CURRENT_OPTIONS_NAME, "~none"));
        } catch (IOException e) {
            return null;
        }
    }


    public void done(Class<?> benchmarkClass) throws IOException {
        Options clone = options.clone();

        String name = benchmarkClass.getName() + ".Transaction.Options";
        System.setProperty(CURRENT_OPTIONS_NAME, name);

        Information.TEMPORARY.save(name, clone);
    }


    public static class Options implements Cloneable, Serializable {
        private int commitInterval;
        private int graphReinitialisationPeriod;

        public int getCommitInterval() {
            return commitInterval;
        }

        public int getGraphReinitialisationPeriod() {
            return graphReinitialisationPeriod;
        }

        @Override
        public Options clone() {
            try {
                return (Options) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e);
            }
        }
    }
}
