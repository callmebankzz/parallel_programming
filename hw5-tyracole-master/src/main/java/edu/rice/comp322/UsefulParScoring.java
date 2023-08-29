package edu.rice.comp322;

import edu.rice.hj.api.HjDataDrivenFuture;
import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module0.*;
import static edu.rice.hj.Module1.asyncAwait;

/**
 * A scorer that works in parallel.
 */
public class UsefulParScoring extends AbstractDnaScoring {

    /**
     * The length of the first sequence.
     */
    private final int xLength;
    /**
     * The length of the second sequence.
     */
    private final int yLength;

    /**
     * The Smith-Waterman matrix.
     */
    private final HjDataDrivenFuture<Integer>[][] s;


    /**
     * <p>main.</p> Takes the names of two files, and in parallel calculates the sequence aligment scores of the two DNA
     * strands that they represent.
     *
     * @param args The names of two files.
     */
    public static void main(final String[] args) throws Exception {
        final ScoringRunner scoringRunner = new ScoringRunner(UsefulParScoring::new);
        scoringRunner.start("UsefulParScoring", args);
    }

    /**
     * Creates a new UsefulParScoring.
     *
     * @param xLength length of the first sequence
     * @param yLength length of the second sequence
     */
    public UsefulParScoring(final int xLength, final int yLength) {
        if (xLength <= 0 || yLength <= 0) {
            throw new IllegalArgumentException("Lengths (" + xLength + ", " + yLength + ") must be positive!");
        }

        this.xLength = xLength;
        this.yLength = yLength;
        //pre allocate the matrix for alignment, dimension+1 for initializations
        s = new HjDataDrivenFuture[xLength + 1][yLength + 1];

        for (int ii = 0; ii < xLength + 1; ii++) {
            for (int jj = 0; jj < yLength + 1; jj++) {
                s[ii][jj] = newDataDrivenFuture();
            }
        }

        //init row
        for (int ii = 1; ii < xLength + 1; ii++) {
            s[ii][0].put(getScore(1, 0) * ii);
        }

        //init column
        for (int jj = 1; jj < yLength + 1; jj++) {
            s[0][jj].put(getScore(0, 1) * jj);
        }
        //init diagonal
        s[0][0].put(0);
    }

    /**
     * Here you should provide an efficient parallel implementation of the Smith-Waterman algorithm that demonstrates
     * real execution time speedup.
     * {@inheritDoc}
     */
    public int scoreSequences(final String x, final String y) throws SuspendableException {

        finish (() -> {

            for (int i = 1; i <= xLength; ++i) {
                for (int j = 1; j <= yLength; ++j) {
                    int finalI = i;
                    int finalJ = j;
                    asyncAwait(s[i-1][j-1], s[i - 1][j], s[i][j - 1], () -> {
                        // the two characters to be compared
                        final char XChar = x.charAt(finalI - 1);
                        final char YChar = y.charAt(finalJ - 1);
                        int firstval = s[finalI - 1][finalJ - 1].get() + getScore(charMap(XChar), charMap(YChar));
                        int secondval = s[finalI - 1][finalJ].get() + getScore(charMap(XChar), 0);
                        int thirdval = s[finalI][finalJ - 1].get() + getScore(0, charMap(YChar));
                        doWork(1);
                        s[finalI][finalJ].put(Math.max(firstval, Math.max(secondval, thirdval)));
                    });
                }
            }
        });

        // final value in the matrix is the score
        return s[xLength][yLength].get();
    }

}

