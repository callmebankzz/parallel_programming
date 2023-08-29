package edu.rice.comp322;

import edu.rice.hj.api.HjMetrics;
import edu.rice.hj.api.SuspendableException;
import edu.rice.hj.runtime.config.HjSystemProperty;
import edu.rice.hj.runtime.metrics.HjMetricsImpl;
import edu.rice.hj.runtime.util.Pair;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static edu.rice.hj.Module0.abstractMetrics;
import static edu.rice.hj.Module0.doWork;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.async;

/**
 * Base class for running Cholesky factorization.
 *
 * @author Shams Imam (shams@rice.edu)
 */
public abstract class CholeskyFactorization {
    public static final String argOutputFormat = "%25s = %-10s \n";

    /**
     * DO NOT CHANGE.
     */
    protected static Pair<Boolean, HjMetrics> runCholeskyFactorization(
        final CholeskyFactorization cf, final String[] args) throws IOException {

        cf.parseArgs(args);
        cf.printArgInfo();
        cf.initialize();

        final HjMetrics[] metrics = {null};
        launchHabaneroApp(
            () -> {
                // async for abstract metrics
                async(() -> {
                    cf.initializeForIteration();
                    cf.runComputation();
                });
            },
            () -> {
                final HjMetrics hjMetrics = abstractMetrics();
                // correct for the extra async
                metrics[0] = new HjMetricsImpl(
                    hjMetrics.totalWork() - cf.blockSize,
                    hjMetrics.criticalPathLength() - cf.blockSize,
                    (1.0 * (hjMetrics.totalWork() - cf.blockSize)) / (hjMetrics.criticalPathLength() - cf.blockSize));

                System.out.println("CholeskyFactorizationSequential: metrics.work = " + metrics[0].totalWork());
                System.out.println("CholeskyFactorizationSequential: metrics.CPL = " + metrics[0].criticalPathLength());
                System.out.println("CholeskyFactorizationSequential: metrics.parallelism = " + metrics[0].idealParallelism());
            });

        final boolean resultValid = cf.cleanupIteration();
        return Pair.factory(resultValid, metrics[0]);
    }

    /**
     * DO NOT CHANGE.
     */
    protected static boolean hasDataItem(final Map<Point, Object> dataStore, final Point key) {
        final Object value = dataStore.get(key);
        return (value != null);
    }

    /**
     * DO NOT CHANGE.
     */
    protected static <T> T readDataItem(final Map<Point, Object> dataStore, final Point key) {
        final Object result = dataStore.get(key);
        if (result == null) {
            throw new IllegalStateException("No item available for " + key);
        }
        return (T) result;
    }

    protected int arraySize = 1000;
    protected int blockSize = 100;
    protected String fileName = "src/main/resources/cholesky/m_" + arraySize + ".in";

    protected final Map<Point, Object> dataStore = new ConcurrentHashMap<Point, Object>();
    protected double[][] inputArray;

    protected void parseArgs(final String[] args) {
        final int argLimit = args.length - 1;
        for (int i = 0; i < argLimit; i++) {
            final String argName = args[i];
            final String argValue = args[i + 1];

            switch (argName) {
                case "-n":
                    arraySize = Integer.parseInt(argValue);
                    fileName = "src/main/resources/cholesky/m_" + arraySize + ".in";
                    i += 1;
                    break;
                case "-b":
                    blockSize = Integer.parseInt(argValue);
                    i += 1;
                    break;
                case "-f":
                    fileName = argValue;
                    i += 1;
                    break;
                default:
                    throw new IllegalStateException("Unsupported option: " + argName);
            }
        }

        inputArray = new double[arraySize][arraySize];
    }

    protected void printArgInfo() {
        System.out.printf(argOutputFormat, "Array size", arraySize);
        System.out.printf(argOutputFormat, "Block size", blockSize);
        System.out.printf(argOutputFormat, "Input file", fileName);

        if (arraySize % blockSize != 0) {
            throw new IllegalStateException("Block size does not divide array size!");
        }
    }

    protected void initialize() throws IOException {

        HjSystemProperty.abstractMetrics.set(true);
        HjSystemProperty.asyncSpawnCostMetrics.set(0);

        dataStore.clear();
        initializeMatrix(inputArray, arraySize, fileName);
    }

    protected void initializeMatrix(
        final double[][] dataArray, final int n, final String fname)
        throws IOException {

        final Reader r = new BufferedReader(new FileReader(fname));
        final StreamTokenizer stok = new StreamTokenizer(r);

        stok.parseNumbers();
        stok.nextToken();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (stok.ttype == StreamTokenizer.TT_EOF) {
                    throw new EOFException("Matrix size incorrect");
                }

                if (stok.ttype != StreamTokenizer.TT_NUMBER) {
                    throw new IOException("A non-number has been read from the file: " + stok.sval);
                }
                dataArray[i][j] = stok.nval;
                stok.nextToken();
            }
        }
    }

    protected void initializeForIteration() {
        {
            final int tileSize = blockSize;
            final int numTiles = arraySize / blockSize;
            // populate the initial data into the store
            for (int i = 0; i < numTiles; i++) {
                for (int j = 0; j <= i; j++) {
                    // Allocate memory for the tiles.
                    final double[][] temp = new double[tileSize][tileSize];
                    // Split the matrix into tiles and write it into the item space at time 0.
                    // The tiles are indexed by tile indices (which are tag values).
                    for (int aI = i * tileSize, tI = 0; tI < tileSize; ++aI, ++tI) {
                        for (int aJ = j * tileSize, tJ = 0; tJ < tileSize; ++aJ, ++tJ) {
                            temp[tI][tJ] = inputArray[aI][aJ];
                        }
                    }
                    storeDataItem(dataStore, Point.newPoint(i, j, 0), temp);
                }
            }
        }
    }

    protected boolean cleanupIteration() {

        final int tileSize = blockSize;
        final int numTiles = arraySize / blockSize;

        // verify all results are available
        int actualCounter = 0;
        int expectedCounter = 0;
        int k;
        for (int i = 0; i < numTiles; ++i) {
            for (int iB = 0; iB < tileSize; ++iB) {
                k = 1;
                for (int j = 0; j <= i; ++j) {
                    if (hasDataItem(dataStore, Point.newPoint(i, j, k))) {
                        actualCounter++;
                    }
                    expectedCounter++;
                    ++k;
                }
            }
        }

        System.out.println("Num result fragments found = " + actualCounter);
        final boolean resultValid = actualCounter == expectedCounter;
        System.out.println("Result Valid = " + resultValid);

        return resultValid;
    }

    protected abstract void runComputation() throws SuspendableException;

    protected abstract void storeDataItem(final Map<Point, Object> dataStore, final Point key, final Object value);

    /**
     * Sequential 'leaf-level' computation.
     * DO NOT CHANGE.
     */
    public final double[][] s1ComputeStepBody2(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore,
        final double[][] aBlock) {

        final int k = pointTag.read(0);

        final double[][] lBlock = new double[tileSize][];

        for (int i = 0; i < tileSize; ++i) {
            lBlock[i] = new double[i + 1];
        }


        for (int kB = 0; kB < tileSize; ++kB) {
            if (aBlock[kB][kB] <= 0) {
                throw new IllegalArgumentException("Not a symmetric positive definite (SPD) matrix");
            }

            lBlock[kB][kB] = Math.sqrt(aBlock[kB][kB]);

            for (int jB = kB + 1; jB < tileSize; ++jB) {
                doWork(1); // One division
                lBlock[jB][kB] = aBlock[jB][kB] / lBlock[kB][kB];
            }

            for (int jBB = kB + 1; jBB < tileSize; ++jBB) {
                for (int iB = jBB; iB < tileSize; ++iB) {
                    doWork(1); // One multiplication
                    aBlock[iB][jBB] = aBlock[iB][jBB] - (lBlock[iB][kB] * lBlock[jBB][kB]);
                }
            }
        }
        final double[][] result = lBlock;
        return result;
    }

    /**
     * Sequential 'leaf-level' computation.
     * DO NOT CHANGE.
     */
    public final double[][] s2ComputeStepBody2(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore,
        final double[][] aBlock, final double[][] liBlock) {

        final int k = pointTag.read(0);
        final int j = pointTag.read(1);

        final double[][] loBlock = new double[tileSize][tileSize];
        for (int i = 0; i < tileSize; ++i) {
            loBlock[i] = new double[tileSize];
        }

        for (int kB = 0; kB < tileSize; ++kB) {
            for (int iB = 0; iB < tileSize; ++iB) {
                doWork(1); // One division
                loBlock[iB][kB] = aBlock[iB][kB] / liBlock[kB][kB];
            }

            for (int jB = kB + 1; jB < tileSize; ++jB) {
                for (int iB = 0; iB < tileSize; ++iB) {
                    doWork(1); // One multiplication
                    aBlock[iB][jB] -= liBlock[jB][kB] * loBlock[iB][kB];
                }
            }

        }
        final double[][] result = loBlock;
        return result;
    }

    /**
     * Sequential 'leaf-level' computation.
     * DO NOT CHANGE.
     */
    public final double[][] s3ComputeStepBody2(
        final int tileSize, final int numTiles,
        final Point pointTag, final Map<Point, Object> dataStore,
        final double[][] aBlock, final double[][] l1Block, final double[][] l2Block) {
        final int k = pointTag.read(0);
        final int j = pointTag.read(1);
        final int i = pointTag.read(2);

        double temp;
        for (int jB = 0; jB < tileSize; ++jB) {
            for (int kB = 0; kB < tileSize; ++kB) {
                temp = 0 - l2Block[jB][kB];
                if (i != j) {
                    for (int iB = 0; iB < tileSize; ++iB) {
                        doWork(1); // One multiplication
                        aBlock[iB][jB] += temp * l1Block[iB][kB];
                    }

                } else {
                    for (int iB = jB; iB < tileSize; ++iB) {
                        doWork(1); // One multiplication
                        aBlock[iB][jB] += temp * l2Block[iB][kB];
                    }
                }
            }
        }
        final double[][] result = aBlock;
        return result;
    }

    /**
     * Point class that represents an integer tuple.
     */
    protected static final class Point {

        public static Point newPoint(final int i) {
            return new Point(new int[]{i});
        }

        public static Point newPoint(final int i, final int j) {
            return new Point(new int[]{i, j});
        }

        public static Point newPoint(final int i, final int j, final int k) {
            return new Point(new int[]{i, j, k});
        }

        private final int[] data;

        private Point(final int[] data) {
            this.data = data;
        }

        public int read(final int index) {
            return data[index];
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Point)) {
                return false;
            }

            final Point pointTag = (Point) o;

            return Arrays.equals(data, pointTag.data);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }

        @Override
        public String toString() {
            return Arrays.toString(data);
        }
    }
}
