package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

import java.io.IOException;
import java.util.Map;

import static edu.rice.hj.Module0.launchHabaneroApp;

/**
 * A version ported from the HJ-CnC implementation of Cholesky benchmark.
 *
 * @author Shams Imam (shams@rice.edu)
 */
public final class CholeskyFactorizationSequential extends CholeskyFactorization {

    /**
     * Main method.
     *
     * @param args command-line args
     * @throws IOException when input file not found.
     */
    public static void main(final String[] args) throws IOException {
        runCholeskyFactorization(new CholeskyFactorizationSequential(), args);
    }

    @Override
    protected void runComputation() throws SuspendableException {
        System.out.println(getClass().getSimpleName() + ".runComputation()");

        final int tileSize = blockSize;
        final int numTiles = arraySize / blockSize;

        controlSingleton(tileSize, numTiles, Point.newPoint(0), dataStore);

        // wait until all data items are available
        int k;
        for (int i = 0; i < numTiles; ++i) {
            for (int iB = 0; iB < tileSize; ++iB) {
                k = 1;
                for (int j = 0; j <= i; ++j) {
                    readDataItem(dataStore, Point.newPoint(i, j, k));
                    ++k;
                }
            }
        }
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

        final double[][] aBlock = readDataItem(dataStore, Point.newPoint(k, k, k));

        final double[][] s1Result = s1ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock);

        storeDataItem(dataStore, Point.newPoint(k, k, k + 1), s1Result);
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

        final double[][] aBlock = readDataItem(dataStore, Point.newPoint(j, k, k));
        final double[][] liBlock = readDataItem(dataStore, Point.newPoint(k, k, k + 1));

        final double[][] s2Result = s2ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock, liBlock);

        storeDataItem(dataStore, Point.newPoint(j, k, k + 1), s2Result);
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

        double[][] l1Block = null;
        final double[][] l2Block;
        final double[][] aBlock = readDataItem(dataStore, Point.newPoint(j, i, k));

        if (i == j) {   // Diagonal tile.
            l2Block = readDataItem(dataStore, Point.newPoint(i, k, k + 1));
            l1Block = l2Block;
        } else {   // Non-diagonal tile.
            l2Block = readDataItem(dataStore, Point.newPoint(i, k, k + 1));
            l1Block = readDataItem(dataStore, Point.newPoint(j, k, k + 1));
        }

        final double[][] s3Result = s3ComputeStepBody2(tileSize, numTiles, pointTag, dataStore, aBlock, l1Block, l2Block);

        storeDataItem(dataStore, Point.newPoint(j, i, k + 1), s3Result);
    }

    /**
     * Stores an item into the data store.
     */
    protected void storeDataItem(final Map<Point, Object> dataStore, final Point key, final Object value) {
        dataStore.put(key, value);
    }
}
