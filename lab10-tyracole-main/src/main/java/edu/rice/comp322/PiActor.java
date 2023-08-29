package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.actors.Actor;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.rice.hj.Module1.finish;
import static edu.rice.hj.Module1.launchHabaneroApp;

/*
 * This class runs a actor version of computing pi by
 * setting a threshold value.
 */
public class PiActor {
    private final int numWorkers;
    private final BigDecimal tolerance;

    /**
     * Constructor.
     */
    public PiActor(final int numWorkers, final BigDecimal setTolerance) {
        this.numWorkers = numWorkers;
        this.tolerance = setTolerance;
    }

    /**
     * Compute Pi up to the specified precision in terms, using numWorkers worker actors.
     */
    public String calcPi() throws SuspendableException {
        final Manager manager = new Manager(numWorkers, tolerance);
        finish(() -> {
            manager.start();
        });
        return manager.getResult();
    }

    // Message classes
    private static class StopMessage {
        public static StopMessage ONLY = new StopMessage();
    }

    private static class WorkMessage {
        public final int term;

        public WorkMessage(final int term) {
            this.term = term;
        }
    }

    private static class ResultMessage {
        public final BigDecimal result;
        public final int workerId;

        public ResultMessage(final BigDecimal result, final int workerId) {
            this.result = result;
            this.workerId = workerId;
        }
    }

    /*
     * A manager actor.
     */
    private static class Manager extends Actor<Object> {

        // start: do not mess with these
        private final int numWorkers;
        private final Worker[] workers;
        // end: do not mess with these

        // use  result to accumulate the value of PI
        private BigDecimal result = BigDecimal.ZERO;

        // use tolerance to decide when it is okay to request termination of workers
        private final BigDecimal tolerance;

        // use counter to track number of workers that have terminated
        private final AtomicInteger numWorkersTerminated = new AtomicInteger(0);
        // use counter to track how many terms have been requested
        private int numTermsRequested = 0;

        /**
         * Constructor.
         */
        public Manager(final int numWorkers, final BigDecimal setTolerance) {
            this.numWorkers = numWorkers;
            this.tolerance = setTolerance;
            this.workers = new Worker[numWorkers];
        }

        /**
         * Actions to take at the start of the Pi estimation.
         */
        public void onPostStart() {
            // now start the workers
            for (int i = 0; i < numWorkers; i++) {
                workers[i] = new PiActor.Worker(this, i);
                workers[i].start();
            }

            // send some work to workers in advance, generateWork() should be useful
            for (int t = 0; t < 10 * numWorkers; t++) {
                generateWork(t % numWorkers);
            }
        }

        /**
         * Generates work for the given worker.
         *
         * @param workerId the id of te worker to send work
         */
        private void generateWork(final int workerId) {
            // send work request to specified worker
            final WorkMessage wm = new WorkMessage(numTermsRequested);
            workers[workerId].send(wm);
            // update the limit for the series requested so far
            numTermsRequested += 1;
        }

        public void requestWorkersToExit() {
            // TODO request all workers to exit by sending them the StopMessage
        }

        protected void process(final Object msg) {
            if (msg instanceof ResultMessage) {
                // a message sent from a worker about the term it computed
                final ResultMessage rm = (ResultMessage) msg;
                result = result.add(rm.result);

                if (rm.result.compareTo(tolerance) <= 0) {
                    // TODO If we reached our precision, we can request workers to terminate
                } else {
                    // TODO else we generate some more work to keep the worker busy
                }
            } else if (msg instanceof StopMessage) {
                // a message sent from a worker that it is terminating
                // TODO track how many workers terminated
                // TODO manager can terminate (via exit()) only if all workers have terminated
            }
        }

        public String getResult() {
            return result.toPlainString();
        }

    }

    /*
     * A worker actor.
     */
    private static class Worker extends Actor<Object> {

        private final Manager manager;
        private final int id;

        public Worker(final Manager manager, final int id) {
            this.manager = manager;
            this.id = id;
        }

        protected void process(final Object msg) {
            if (msg instanceof StopMessage) {
                // TODO let manager know worker is terminating by sending a message.
                // TODO Then, terminate this actor.
            } else if (msg instanceof WorkMessage) {
                // manager requested computation of a term
                final WorkMessage wm = (WorkMessage) msg; // do some more computation
                final BigDecimal result = PiUtil.calculateBbpTerm(wm.term);
                manager.send(new ResultMessage(result, id));
            }
        }
    }
}
