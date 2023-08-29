package edu.rice.comp322;

import junit.framework.TestCase;

import static edu.rice.hj.Module0.*;
import static edu.rice.hj.runtime.config.HjConfiguration.readIntProperty;

import edu.rice.hj.runtime.config.HjSystemProperty;

import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.security.AccessControlException;

public class Lab11PerformanceTest extends TestCase {

    private static final ArrayData kernel1 = new ArrayData(3, 3);
    private static final ArrayData kernel2 = new ArrayData(5, 5);

    static {
        kernel1.set(0, 0, 0);
        kernel1.set(1, 0, -1);
        kernel1.set(2, 0, 0);
        kernel1.set(0, 1, -1);
        kernel1.set(1, 1, 4);
        kernel1.set(2, 1, -1);
        kernel1.set(0, 2, 0);
        kernel1.set(1, 2, -1);
        kernel1.set(2, 2, 0);

        kernel2.set(0, 0, 0);
        kernel2.set(1, 0, -1);
        kernel2.set(2, 0, 0);
        kernel2.set(3, 0, -1);
        kernel2.set(4, 0, 0);
        kernel2.set(0, 1, -1);
        kernel2.set(1, 1, 4);
        kernel2.set(2, 1, -1);
        kernel2.set(3, 1, 4);
        kernel2.set(4, 1, -1);
        kernel2.set(0, 2, 0);
        kernel2.set(1, 2, -1);
        kernel2.set(2, 2, 0);
        kernel2.set(3, 2, -1);
        kernel2.set(4, 2, 0);
        kernel2.set(0, 3, -1);
        kernel2.set(1, 3, 4);
        kernel2.set(2, 3, -1);
        kernel2.set(3, 3, 4);
        kernel2.set(4, 3, -1);
        kernel2.set(0, 4, 0);
        kernel2.set(1, 4, -1);
        kernel2.set(2, 4, 0);
        kernel2.set(3, 4, -1);
        kernel2.set(4, 4, 0);
    }

    /**
     * Java exposes the image data using getRGB
     * @param filename the filename of the picture.
     * @return the image array data.
     * @throws IOException io exception.
     */
    private static ArrayData[] getArrayDataFromImage(String filename)
            throws IOException  {
        BufferedImage inputImage = ImageIO.read(new File(filename));

        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        // get the data
        int[] rgbData = inputImage.getRGB(0, 0, width, height, null, 0, width);

        // massage it into 8 bit format
        ArrayData reds = new ArrayData(width, height);
        ArrayData greens = new ArrayData(width, height);
        ArrayData blues = new ArrayData(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbValue = rgbData[y * width + x];
                reds.set(x, y, (rgbValue >>> 16) & 0xFF);
                greens.set(x, y, (rgbValue >>> 8) & 0xFF);
                blues.set(x, y, rgbValue & 0xFF);
            }
        }
        return new ArrayData[] { reds, greens, blues };
    }

    /**
     * write to output png.
     * @param filename the name of the file.
     * @param redGreenBlue rgb values.
     * @throws IOException io exception.
     */
    public static void writeOutputImage(String filename,
            ArrayData[] redGreenBlue) throws IOException  {
        ArrayData reds = redGreenBlue[0];
        ArrayData greens = redGreenBlue[1];
        ArrayData blues = redGreenBlue[2];
        BufferedImage outputImage = new BufferedImage(reds.width, reds.height,
                                  BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < reds.height; y++) {
            for (int x = 0; x < reds.width; x++) {
                int red = seqBound(reds.get(x, y), 256);
                int green = seqBound(greens.get(x, y), 256);
                int blue = seqBound(blues.get(x, y), 256);
                outputImage.setRGB(x, y, (red << 16) | (green << 8) | blue | -0x01000000);
            }
        }
        // we are writing to PNG format, but many other common format supported
        try {
            ImageIO.write(outputImage, "PNG", new File(filename));
        } catch (AccessControlException a) {
            /*
             * Programs do not have filesystem permissions on the autograder, so
             * ignore this exception if we hit it.
             */
        }
        return;
    }

    private static int seqBound(int value, int endIndex)  {
        if (value < 0) {
            return 0;
        }

        if (value < endIndex) {
            return value;
        }
        return endIndex - 1;
    }

    private static ArrayData seqConvolve(ArrayData inputData, ArrayData kernel,
            int kernelDivisor)  {
        int inputWidth = inputData.width;
        int inputHeight = inputData.height;
        int kernelWidth = kernel.width;
        int kernelHeight = kernel.height;

        if ((kernelWidth <= 0) || ((kernelWidth & 1) != 1)) {
            throw new IllegalArgumentException("Kernel must have odd width");
        }
        if ((kernelHeight <= 0) || ((kernelHeight & 1) != 1)) {
            throw new IllegalArgumentException("Kernel must have odd height");
        }

        int kernelWidthRadius = kernelWidth >>> 1;
        int kernelHeightRadius = kernelHeight >>> 1;

        ArrayData outputData = new ArrayData(inputWidth, inputHeight);

        // this is the part to be parallelized
        for (int i = inputWidth - 1; i >= 0; i--) {
            for (int j = inputHeight - 1; j >= 0; j--) {
                double newValue = 0.0;
                for (int kw = kernelWidth - 1; kw >= 0; kw--) {
                    for (int kh = kernelHeight - 1; kh >= 0; kh--)  {
                        newValue += kernel.get(kw, kh) * inputData.get(
                                           seqBound(i + kw - kernelWidthRadius, inputWidth),
                                           seqBound(j + kh - kernelHeightRadius, inputHeight));
                    }
                }
                // the kernel divisor is to normalize the image (otherwise it can get darker or brighter)
                outputData.set(i, j, (int)Math.round(newValue / kernelDivisor));
            }
        }
        return outputData;
    }

    private void testDriver(final String lbl, final ArrayData kernel,
            final String inputPath, final String outputPath,
            final int kernelDivisor, final double expectedSpeedup) throws IOException {
        final ArrayData[] input = new ArrayData[3];
        final ArrayData[] seqInput = new ArrayData[3];

        launchHabaneroApp(() -> {
            final int numWorkers = readIntProperty(HjSystemProperty.numWorkers);
            PerfTestUtils.PerfTestResults results = PerfTestUtils.runPerfTest(
                lbl,
                () -> {
                    // Parallel setup
                    try {
                        final ArrayData[] newInput = getArrayDataFromImage(
                            inputPath);
                        input[0] = newInput[0];
                        input[1] = newInput[1];
                        input[2] = newInput[2];
                    } catch (IOException io) {
                        throw new RuntimeException(io);
                    }
                },
                () -> {
                    // Parallel computation
                    for (int i = 0; i < input.length; i++) {
                        input[i] = Lab11.convolve(input[i], kernel,
                            kernelDivisor);
                    }
                }, null,
                () -> {
                    // Sequential setup
                    try {
                        final ArrayData[] newInput = getArrayDataFromImage(
                                inputPath);
                        seqInput[0] = newInput[0];
                        seqInput[1] = newInput[1];
                        seqInput[2] = newInput[2];
                    } catch (IOException io) {
                        throw new RuntimeException(io);
                    }
                },
                () -> {
                    // Sequential computation
                    for (int i = 0; i < seqInput.length; i++) {
                        seqInput[i] = seqConvolve(seqInput[i], kernel,
                            kernelDivisor);
                    }
                }, null,
                null, 3, 2, numWorkers);

            for (int i = 0; i < input.length; i++) {
                ArrayData calc = input[i];
                ArrayData ref = seqInput[i];

                assertEquals("Mismatch in expected height of generated image",
                        ref.height, calc.height);
                assertEquals("Mismatch in expected width of generated image",
                        ref.width, calc.width);

                for (int j = 0; j < calc.height; j++) {
                    for (int k = 0; k < calc.width; k++) {
                        double refVal = ref.get(j, k);
                        double calcVal = calc.get(j, k);
                        assertEquals("Mismatch in computed value at " +
                                "coordinate (" + i + ", " + j + ", " + k + ")",
                                refVal, calcVal);
                    }
                }
            }

            final double speedup = (double)results.seqTime / (double)results.parTime;
            System.out.println("\nConvolution of image = " + inputPath +
                    " w/ (" + kernel.height + "x" + kernel.width + ") " +
                    "kernel yielded speedup of " + speedup + "x (sequential " +
                    "time = " + results.seqTime + " ms, parallel time = " +
                    results.parTime + " ms) using HJlib");
            try {
                writeOutputImage(outputPath, input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertTrue("Expected speedup of parallel convolution " +
                    "implementation to be at least " + expectedSpeedup + "x, " +
                    "but was " + speedup, speedup >= expectedSpeedup);
        });

    }

    // private void streamsTestDriver(final String lbl, final ArrayData kernel,
    //         final String inputPath, final String outputPath,
    //         final int kernelDivisor) throws IOException {
    //     final ArrayData[] input = new ArrayData[3];
    //     final ArrayData[] seqInput = new ArrayData[3];

    //     final int numWorkers = readIntProperty(HjSystemProperty.numWorkers);
    //     PerfTestUtils.PerfTestResults results = PerfTestUtils.runPerfTestNoSuspend(
    //         lbl,
    //         () -> {
    //             // Parallel setup
    //             try {
    //                 final ArrayData[] newInput = getArrayDataFromImage(
    //                     inputPath);
    //                 input[0] = newInput[0];
    //                 input[1] = newInput[1];
    //                 input[2] = newInput[2];
    //             } catch (IOException io) {
    //                 throw new RuntimeException(io);
    //             }
    //         },
    //         () -> {
    //             // Parallel computation
    //             for (int i = 0; i < input.length; i++) {
    //                 input[i] = Lab5.convolveWithJavaStreams(input[i], kernel,
    //                     kernelDivisor);
    //             }
    //         }, null,
    //         () -> {
    //             // Sequential setup
    //             try {
    //                 final ArrayData[] newInput = getArrayDataFromImage(
    //                         inputPath);
    //                 seqInput[0] = newInput[0];
    //                 seqInput[1] = newInput[1];
    //                 seqInput[2] = newInput[2];
    //             } catch (IOException io) {
    //                 throw new RuntimeException(io);
    //             }
    //         },
    //         () -> {
    //             // Sequential computation
    //             for (int i = 0; i < seqInput.length; i++) {
    //                 seqInput[i] = seqConvolve(seqInput[i], kernel,
    //                     kernelDivisor);
    //             }
    //         }, null,
    //         null, 3, 2, numWorkers);

    //     for (int i = 0; i < input.length; i++) {
    //         ArrayData calc = input[i];
    //         ArrayData ref = seqInput[i];

    //         assertEquals("Mismatch in expected height of generated image",
    //                 ref.height, calc.height);
    //         assertEquals("Mismatch in expected width of generated image",
    //                 ref.width, calc.width);

    //         for (int j = 0; j < calc.height; j++) {
    //             for (int k = 0; k < calc.width; k++) {
    //                 double refVal = ref.get(j, k);
    //                 double calcVal = calc.get(j, k);
    //                 assertEquals("Mismatch in computed value at " +
    //                         "coordinate (" + i + ", " + j + ", " + k + ")",
    //                         refVal, calcVal);
    //             }
    //         }
    //     }

    //     final double speedup = (double)results.seqTime / (double)results.parTime;
    //     System.out.println("\nConvolution of image = " + inputPath +
    //             " w/ (" + kernel.height + "x" + kernel.width + ") " +
    //             "kernel yielded speedup of " + speedup + "x (sequential " +
    //             "time = " + results.seqTime + " ms, parallel time = " +
    //             results.parTime + " ms) using Java Streams");
    //     assertTrue("Expected speedup of parallel convolution " +
    //             "implementation to be at least 1.2x, but was " + speedup,
    //             speedup >= 1.2);

    //     writeOutputImage(outputPath, input);
    // }


    public void testKobeKernel1() throws IOException {
        final String lbl = PerfTestUtils.getTestLabel();
        testDriver(lbl, kernel1, "src/main/resources/kobe.jpg",
                "src/main/resources/kobe.1.jpg", 1, 5.0);
    }

    public void testKobeKernel2() throws IOException {
        final String lbl = PerfTestUtils.getTestLabel();
        testDriver(lbl, kernel2, "src/main/resources/kobe.jpg",
                "src/main/resources/kobe.2.jpg", 1, 8.0);
    }

    // public void testKobeKernel1Streams() throws IOException {
    //     final String lbl = PerfTestUtils.getTestLabel();
    //     streamsTestDriver(lbl, kernel1, "src/main/resources/kobe.jpg",
    //             "src/main/resources/kobe.1.jpg", 1);
    // }

    // public void testKobeKernel2Streams() throws IOException {
    //     final String lbl = PerfTestUtils.getTestLabel();
    //     streamsTestDriver(lbl, kernel2, "src/main/resources/kobe.jpg",
    //             "src/main/resources/kobe.2.jpg", 1);
    // }
}
