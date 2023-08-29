package edu.rice.comp322;

import edu.rice.hj.api.SuspendableException;

import static edu.rice.hj.Module1.*;

/**
 * Wrapper class for implementing image convolution.
 */
public class Lab11 {

    // gets around end cases
    private static int bound(int value, int endIndex)  {
        if (value < 0) {
            return 0;
        }

        if (value < endIndex) {
            return value;
        }
        return endIndex - 1;
    }

    /**
     * Main convolve function.
     *
     * four nested loops (i, j, kw, kh). For this method you should use HJlib.
     * Which loops are parallelizable? Which are not?
     *
     * @param inputData the input data.
     * @param kernel the kernel matrix.
     * @param kernelDivisor the kernel divisor.
     * @return the arraydata after convolving.
     */
    public static ArrayData convolve(ArrayData inputData, ArrayData kernel,
            int kernelDivisor) throws SuspendableException {
        final int inputWidth = inputData.width;
        final int inputHeight = inputData.height;
        final int kernelWidth = kernel.width;
        final int kernelHeight = kernel.height;

        if ((kernelWidth <= 0) || ((kernelWidth & 1) != 1)) {
            throw new IllegalArgumentException("Kernel must have odd width");
        }
        if ((kernelHeight <= 0) || ((kernelHeight & 1) != 1)) {
            throw new IllegalArgumentException("Kernel must have odd height");
        }

        final int kernelWidthRadius = kernelWidth >>> 1;
        final int kernelHeightRadius = kernelHeight >>> 1;

        final ArrayData outputData = new ArrayData(inputWidth, inputHeight);

        // this is the part to be parallelized
        forallChunked(0, inputWidth - 1, inputWidth/16, (i) -> {
            for (int j = inputHeight - 1; j >= 0; j--) {
                double newValue = 0.0;
                for (int kw = kernelWidth - 1; kw >= 0; kw--) {
                    for (int kh = kernelHeight - 1; kh >= 0; kh--)  {
                        newValue += kernel.get(kw, kh) * inputData.get(
                                bound(i + kw - kernelWidthRadius, inputWidth),
                                bound(j + kh - kernelHeightRadius, inputHeight));
                    }
                }
                /*
                 * The kernel divisor is to normalize the image (otherwise it
                 * can get darker or brighter).
                 */
                outputData.set(i, j, (int)Math.round(newValue / kernelDivisor));
            }
        });
        return outputData;
    }
}
