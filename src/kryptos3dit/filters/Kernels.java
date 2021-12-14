package kryptos3dit.filters;

/**
 * Contains various kernels used by {@code Filters.java}
 * @author Jaideep
 */
final class Kernels {

    // Sobel Kernel for x-direction
    public static final int[][] sobelKernelX = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    // Sobel Kernel for y-direction
    public static final int[][] sobelKernelY = {
        {-1, -2, -1},
        { 0,  0,  0},
        { 1,  2,  1}
    };

    /**
     * Takes intensity as parameter and returns a kernel
     * that will be convolved with the image. Central value 
     * is positive in order to give more weight to the pixel in
     * the center. This is effectively a high pass filter.
     * @param intensity Intensity of the kernel
     * @return          A kernel with the applied intensity
     */
    public static int[][] getSharpenKernel(int intensity) {

        int[][] kernel = {
            {0, -1 * intensity, 0},
            {-1 * intensity, (4 * intensity) + 1, -1 * intensity},
            {0, -1 * intensity, 0}
        };

        return kernel;
    }
}
