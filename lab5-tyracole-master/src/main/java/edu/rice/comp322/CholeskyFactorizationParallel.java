package edu.rice.comp322;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.SuspendableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.rice.hj.Module0.newDataDrivenFuture;
import static edu.rice.hj.Module1.asyncAwait;

/**
 * A version ported from the HJ-CnC implementation of Cholesky benchmark.
 *
 * @author Shams Imam (shams@rice.edu)
 */
public final class CholeskyFactorizationParallel extends CholeskyFactorization {

    /**
     * Main method.
     *
     * @param args command-line args
     * @throws IOException when input file not found.
     */
    public static void main(final String[] args) throws IOException {
        runCholeskyFactorization(new CholeskyFactorizationParallel(), args);
    }

    @Override
    protected void runComputation() throws SuspendableException {
        System.out.println(getClass().getSimpleName() + ".runComputation()");

        final int tileSize = blockSize;
        final int numTiles = arraySize / blockSize;

        controlSingleton(tileSize, numTiles, Point.newPoint(0), dataStore);
        final List<HjFuture<double[][]>> tasks = new ArrayList<>();

        // wait until all data items are available
        int k;
        for (int i = 0; i < numTiles; ++i) {
            for (int iB = 0; iB < tileSize; ++iB) {
                k = 1;
                for (int j = 0; j <= i; ++j) {
                    Object dataItem = readDataItem(dataStore, Point.newPoint(i, j, k));
                    if (dataItem instanceof HjFuture) {
                        @SuppressWarnings("unchecked")
                        final HjFuture<double[][]> task = (HjFuture<double[][]>) dataItem;
                        tasks.add(task);
                    }
                    ++k;
                }
            }
        }

        asyncAwait(tasks, () -> {
            // done!
        });
    }

    /**
     * Triggers instances of kComputeStep.
     */
    public void controlSingleton(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        kComputeStep(tileSize, numTiles, pointTag, dataStore);
    }

    /**
     * Triggers instances of controlStep1.
     */
    public void kComputeStep(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        for (int k = 0; k < numTiles; ++k) {
            controlStep1(tileSize, numTiles, Point.newPoint(k), dataStore);
        }
    }

    /**
     * Triggers instances of s1ComputeStep and kjComputeStep.
     */
    public void controlStep1(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        s1ComputeStep(tileSize, numTiles, pointTag, dataStore);
        kjComputeStep(tileSize, numTiles, pointTag, dataStore);
    }

    /**
     * Triggers execution of s1ComputeStepBody (possibly asynchronously).
     */
    public void s1ComputeStep(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {

        final int k = pointTag.read(0);

        // TODO Use the hint below to correct the following code.
        // Use the provided code below to fix s2ComputeStep and s3ComputeStep.
        //final double[][] aBlock = readDataItem(dataStore, Point.newPoint(k, k, k));

        //final double[][] s1Result = s1ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock);

        //storeDataItem(dataStore, Point.newPoint(k, k, k + 1), s1Result);

        /*
         * HINT: This is how the solution would look like for this step. You can uncomment this code to complete this
         * section.
         */

        final HjFuture<double[][]> aBlockFuture = readDataItem(dataStore, Point.newPoint(k, k, k));

        final HjDataDrivenFuture<double[][]> s1Future = newDataDrivenFuture();
        storeDataItem(dataStore, Point.newPoint(k, k, k + 1), s1Future); // store DDF immediately into datastore

        asyncAwait(aBlockFuture, () -> {
            final double[][] aBlock = aBlockFuture.safeGet();
            final double[][] s1Result = s1ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock);
            s1Future.put(s1Result);
        });
    }

    /**
     * Triggers execution of controlStep2.
     */
    public void kjComputeStep(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        final int k = pointTag.read(0);
        for (int j = k + 1; j < numTiles; ++j) {
            controlStep2(tileSize, numTiles, Point.newPoint(k, j), dataStore);
        }
    }

    /**
     * Triggers instances of s2ComputeStep and kjiComputeStep.
     */
    public void controlStep2(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        s2ComputeStep(tileSize, numTiles, pointTag, dataStore);
        kjiComputeStep(tileSize, numTiles, pointTag, dataStore);
    }

    /**
     * Triggers execution of s2ComputeStepBody (possibly asynchronously).
     */
    public void s2ComputeStep(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {

        final int k = pointTag.read(0);
        final int j = pointTag.read(1);

        // TODO readDataItem() will now return HjFuture<double[][]> instances.
        // TODO extract them individually and have your asyncAwait ready.

        final HjFuture<double[][]> aBlockFuture = readDataItem(dataStore, Point.newPoint(j, k, k));
        final HjFuture<double[][]> liBlockFuture = readDataItem(dataStore, Point.newPoint(k, k, k + 1));

        final HjDataDrivenFuture<double[][]> s2Future = newDataDrivenFuture();
        storeDataItem(dataStore, Point.newPoint(j, k, k + 1), s2Future);

        // TODO this computation should be inside an asyncAwait which populates a DDF
        asyncAwait(aBlockFuture, liBlockFuture, () -> {
            final double[][] aBlock = aBlockFuture.safeGet();
            final double[][] liBlock = liBlockFuture.safeGet();
            final double[][] s2Result = s2ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock, liBlock);
            s2Future.put(s2Result);
        });

        // TODO Do not forget to store the DDF
        //storeDataItem(dataStore, Point.newPoint(j, k, k + 1), s2Result);
    }

    /**
     * Triggers execution of controlStep3.
     */
    public void kjiComputeStep(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        final int k = pointTag.read(0);
        final int j = pointTag.read(1);
        for (int i = k + 1; i <= j; ++i) {
            controlStep3(tileSize, numTiles, Point.newPoint(k, j, i), dataStore);
        }
    }

    /**
     * Triggers instances of s3ComputeStep.
     */
    public void controlStep3(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore) {
        s3ComputeStep(tileSize, numTiles, pointTag, dataStore);
    }

    /**
     * Triggers execution of s3ComputeStepBody (possibly asynchronously).
     */
    public void s3ComputeStep(
            final int tileSize, final int numTiles,
            final Point pointTag, final Map<Point, Object> dataStore) {

        final int k = pointTag.read(0);
        final int j = pointTag.read(1);
        final int i = pointTag.read(2);

        // TODO readDataItem() will now return HjFuture<double[][]> instances.
        // TODO extract them individually and have your asyncAwait ready.

        final HjFuture<double[][]> l1BlockFuture;
        final HjFuture<double[][]> l2BlockFuture;
        final HjFuture<double[][]> aBlockFuture = readDataItem(dataStore, Point.newPoint(j, i, k));

        if (i == j) {   // Diagonal tile.
            l2BlockFuture = readDataItem(dataStore, Point.newPoint(i, k, k + 1));
            l1BlockFuture = l2BlockFuture;
        } else {   // Non-diagonal tile.
            l2BlockFuture = readDataItem(dataStore, Point.newPoint(i, k, k + 1));
            l1BlockFuture = readDataItem(dataStore, Point.newPoint(j, k, k + 1));
        }

        final HjDataDrivenFuture<double[][]> s3Future = newDataDrivenFuture();
        storeDataItem(dataStore, Point.newPoint(j, i, k + 1), s3Future);

        // TODO this computation should be inside an asyncAwait which populates a DDF
        //final double[][] s3Result = s3ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock, l1Block, l2Block);

        asyncAwait(aBlockFuture, l1BlockFuture, l2BlockFuture, () -> {
            final double[][] aBlock = aBlockFuture.safeGet();
            final double[][] l1Block = l1BlockFuture.safeGet();
            final double[][] l2Block = l2BlockFuture.safeGet();
            final double[][] s3Result = s3ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock, l1Block, l2Block);
            s3Future.put(s3Result);
        });

        // TODO Do not forget to store the DDF
        //storeDataItem(dataStore, Point.newPoint(j, i, k + 1), s3Result);
    }

    /**
     * Stores an item into the data store.
     */
    protected void storeDataItem(final Map<Point, Object> dataStore, final Point key, final Object value) {
        // TODO Handle the case where the value is not an instance of HjFuture
        // TODO For e.g. if value is not a HjFuture, create a new DDF and put the DDF in the data store.
        // TODO Do not forget to set the value into the DDF using the ddf.put() call.
        if(!(value instanceof HjDataDrivenFuture)){
            final HjDataDrivenFuture<Object> future = newDataDrivenFuture();
            future.put(value);
            dataStore.put(key, future);
        } else {
            dataStore.put(key, value);
        }
    }
}
