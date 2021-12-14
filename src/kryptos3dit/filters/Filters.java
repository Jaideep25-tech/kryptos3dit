package kryptos3dit.filters;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Contains methods to apply filters on images.
 * @author Jaideep
 */
public final class Filters {

    /**
     * Mirrors an image, works in {@code O(height * width)}
     * @param image     The input image
     * @param vertical  A boolean variable which is true if image is to be mirrored vertically
     * @return          The mirrored image
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage mirror(BufferedImage image, boolean vertical)
                                        throws ArrayIndexOutOfBoundsException {

        // Make a new object in memory
        BufferedImage result = Helpers.deepCopy(image);
        int height = result.getHeight();
        int width = result.getWidth();

        if (vertical) {
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width / 2; ++j) {

                    // Swap pixel at (j, i) position with pixel at (width - j - 1, i) position
                    int temp = result.getRGB(j, i);
                    result.setRGB(j, i, result.getRGB(width - j - 1, i));
                    result.setRGB(width - j - 1, i, temp);
                }
            }
        } 
        else {
            for (int j = 0; j < width; ++j) {
                for (int i = 0; i < height / 2; ++i) {

                    // Swap pixel at (j, i) position with pixel at (j, height - i - 1) position
                    int temp = result.getRGB(j, i);
                    result.setRGB(j, i, result.getRGB(j, height - i - 1));
                    result.setRGB(j, height - i - 1, temp);
                }
            }
        }

        return result;
    }

    /**
     * Produces a clipped image rotated by {@code angle} degrees,
     * by treating the image as a rectangle on the cartesian plane
     * and using bilinear interpolation to find the pixel value at every co-ordinate that 
     * will not have a black color after rotation. For more information about the math involved,
     * visit https://en.wikipedia.org/wiki/Bilinear_interpolation
     * @param image The input image
     * @param angle Floating-point degree, anti-clockwise 
     * @return      The rotated image
     */
    public static BufferedImage rotate(BufferedImage image, double angle)
                                    throws ArrayIndexOutOfBoundsException {
                                                    
        int width = image.getWidth();
        int height = image.getHeight();
        int imageType = BufferedImage.TYPE_INT_RGB;

        if (image.isAlphaPremultiplied()) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }

        BufferedImage result = new BufferedImage(width, height, imageType);

        // Convert angle from degree to radians
        angle = Helpers.toRadians(angle);

        // Get centre co-ordinates
        int centreX = width / 2;
        int centreY = height / 2;

        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {

                result.setRGB(j, i, Color.black.getRGB());

                // Raster to cartesian
                int[] points = Helpers.toCartesian(j, i, centreX, centreY);

                // Continue if point is the origin
                if (points[0] == 0 && points[1] == 0) {
                    continue;
                }

                // Cartesian to polar form
                double[] polarData = Helpers.toPolar(points[0], points[1]);

                // Subtract the angle from the polar angle
                polarData[1] -= angle;

                // Use the new polar angle to make new cartesian points
                double[] cartesianPoints = Helpers.toCartesian(polarData);
                
                // Use the new cartesian points to make new raster points
                double[] rasterPoints = Helpers.toRaster(cartesianPoints, centreX, centreY);


                int[] floored = Helpers.floorPoints(rasterPoints);
                int[] ceiled = Helpers.ceilPoints(rasterPoints);
                
                // Check for bounds
                if (Helpers.isOutOfBounds(floored[0], width)  || 
                    Helpers.isOutOfBounds(floored[1], height) || 
                    Helpers.isOutOfBounds(ceiled[0], width)   || 
                    Helpers.isOutOfBounds(ceiled[1], height)) {
                        
                        continue;
                }

                // Compute the delta
                double deltaX = rasterPoints[0] - (double) floored[0];
                double deltaY = rasterPoints[1] - (double) floored[1];

                // Use the floored and ceiled points to get four colors
                Color topLeft = new Color(image.getRGB(floored[0], floored[1]));
                Color topRight = new Color(image.getRGB(ceiled[0], floored[1]));
                Color bottomLeft = new Color(image.getRGB(floored[0], ceiled[1]));
                Color bottomRight = new Color(image.getRGB(ceiled[0], ceiled[1]));

                // Perform bilinear interpolation on these four colors
                int finalColor = Helpers.bilinearInterpolation(topLeft, topRight, bottomLeft,
                                                                bottomRight, deltaX, deltaY);
                    
                result.setRGB(j, i, finalColor);
            }
        }

        return result;
    }

    /**
     * Detects edges in an image.
     * For the math, check https://en.wikipedia.org/wiki/Sobel_operator
     * @param image The input image
     * @return      An image that has outlined edges in it
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage detectEdges(BufferedImage image)
                            throws ArrayIndexOutOfBoundsException {

        int width = image.getWidth();
        int height = image.getHeight();
        int imageType = BufferedImage.TYPE_INT_RGB;

        if (image.isAlphaPremultiplied()) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }

        BufferedImage result = new BufferedImage(width, height, imageType);

        for (int i = 0; i < height; ++i) {

            int[] sobelSumX = new int[3];
            int[] sobelSumY = new int[3];
            int[] sobelXY = new int[3];

            for (int j = 0; j < width; ++j) {

                // Set initial values to zero
                for (int k = 0; k < 3; ++k) {
                    sobelSumX[k] = 0;
                    sobelSumY[k] = 0;
                }

                for (int m = 0; m < 3; ++m) {
                    for (int n = 0; n < 3; ++n) {

                        // Compute indices to operate upon
                        int indexI = i + m - 1;
                        int indexJ = j + n - 1;

                        // Check for bounds
                        if (Helpers.isOutOfBounds(indexI, height) || 
                            Helpers.isOutOfBounds(indexJ, width)) {
                                
                                continue;
                        }

                        Color color = new Color(image.getRGB(indexJ, indexI));

                        // Add the R,G,B values in X and Y direction
                        sobelSumX[0] += Kernels.sobelKernelX[m][n] * color.getRed();
                        sobelSumX[1] += Kernels.sobelKernelX[m][n] * color.getGreen();
                        sobelSumX[2] += Kernels.sobelKernelX[m][n] * color.getBlue();
                        sobelSumY[0] += Kernels.sobelKernelX[m][n] * color.getRed();
                        sobelSumY[1] += Kernels.sobelKernelX[m][n] * color.getGreen();
                        sobelSumY[2] += Kernels.sobelKernelX[m][n] * color.getBlue();
                    }
                }

                // Compute the vector sum for each color value
                for (int k = 0; k < 3; ++k) {

                    sobelXY[k] = (int) Math.round(Math.sqrt((
                                        sobelSumX[k] * sobelSumX[k] + sobelSumY[k] * sobelSumY[k])));
                    
                    sobelXY[k] = Helpers.truncateIfNeeded(sobelXY[k]);
                }

                // Get the final color
                Color color = new Color(sobelXY[0], sobelXY[1], sobelXY[2]);
                result.setRGB(j, i, color.getRGB());
            }
        }

        return result;
    }

    /**
     * Brightens an image.
     * @param image The input image
     * @param dial  A dial which can take postive values upto 1.0
     * @return      The brightened image
     */
    public static BufferedImage brighten(BufferedImage image, double dial) {

        // Return null for an incorrect dial value
        if (dial < 0.0 || dial > 1.0) {
            return null;
        }

        BufferedImage result = Helpers.deepCopy(image);
        Helpers.lightDial(result, dial);

        return result;
    }

    /**
     * Darkens an image.
     * @param image The input image
     * @param dial  A dial which can take negative values, -1.0 being the lowest.
     * @return      The darkened image
     */
    public static BufferedImage darken(BufferedImage image, double dial) {

        // Return null for an incorrect dial value
        if (dial > 0.0 || dial < -1.0) {
            return null;
        }

        BufferedImage result = Helpers.deepCopy(image);
        Helpers.lightDial(result, dial);

        return result;
    }

    /**
     * Takes an image and makes it grayscale.
     * @param image The input image
     * @return      The grayscaled image
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage grayscale(BufferedImage image)
                        throws ArrayIndexOutOfBoundsException {

        BufferedImage img = Helpers.deepCopy(image);

        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {

                Color color = new Color(img.getRGB(i, j));

                int tr = (int) (color.getRed() * 0.2126);
                int tg = (int) (color.getGreen() * 0.7152);
                int tb = (int) (color.getBlue() * 0.0722);
                int sum = tr + tg + tb;

                Color shadeOfGray = new Color(sum, sum, sum);
                img.setRGB(i, j, shadeOfGray.getRGB());
            }
        }

        return img;
    }

    /**
     * Apply the sepia effect.
     * @param image The input image
     * @return      The image with the sepia effect
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage sepia(BufferedImage image) 
                        throws ArrayIndexOutOfBoundsException {

        BufferedImage img = Helpers.deepCopy(image);

        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {

                Color color = new Color(img.getRGB(i, j));

                int tr = (int) (color.getRed() * 0.393 + color.getGreen() * 0.769 + color.getBlue() * 0.189);
                int tg = (int) (color.getRed() * 0.349 + color.getGreen() * 0.686 + color.getBlue() * 0.168);
                int tb = (int) (color.getRed() * 0.272 + color.getGreen() * 0.534 + color.getBlue() * 0.131);

                int red = tr > 255 ? 255 : tr;
                int green = tg > 255 ? 255 : tg;
                int blue = tb > 255 ? 255 : tb;

                Color newColor = new Color(red, green, blue);
                img.setRGB(i, j, newColor.getRGB());
            }
        }

        return img;
    }

    /**
     * Inverts an image.
     * @param image The input image
     * @return      The negative/inverted image
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage negative(BufferedImage image) 
                            throws ArrayIndexOutOfBoundsException {
        
        BufferedImage img = Helpers.deepCopy(image);

        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {

                Color color = new Color(img.getRGB(i, j));

                int tr = (int) (255 - color.getRed());
                int tg = (int) (255 - color.getGreen());
                int tb = (int) (255 - color.getBlue());

                Color newColor = new Color(tr, tg, tb);
                img.setRGB(i, j, newColor.getRGB());
            }
        }

        return img;
    }

    /**
     * Add a watermark to an image.
     * @param image         The input image
     * @param watermarkText A string that contains the watermark content
     * @return              Image with the applied watermark
     * @throws NullPointerException
     */
    public static BufferedImage addWatermark(BufferedImage image, String watermarkText)
                                                                throws NullPointerException {
        
        BufferedImage img = Helpers.deepCopy(image);
        Graphics2D graphics = (Graphics2D) img.getGraphics();

        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);

        graphics.setComposite(alphaChannel);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.BOLD, 64));

        FontMetrics fontMetrics = graphics.getFontMetrics();
        Rectangle2D rectangle = fontMetrics.getStringBounds(watermarkText, graphics);

        int centerX = (img.getWidth() - (int) rectangle.getWidth()) / 2;
        int centerY = img.getHeight() / 2;

        graphics.drawString(watermarkText, centerX, centerY);
        graphics.dispose();

        return img;
    }

    /**
     * Apply box blur on an image in {@code O(height * width)}.
     * @param source        The input image
     * @param target        The image that will store the result of boxBlur
     * @param kernelRadius  An integer value in the range 
     *                      [1, min({@code source.height} - 1, {@code source.width} - 1)]
     * @throws ArrayIndexOutOfBoundsException
     */
    public static void boxBlur(BufferedImage source, BufferedImage target, int kernelRadius) 
                                                        throws ArrayIndexOutOfBoundsException {

        if (kernelRadius < 0 || kernelRadius >= Math.min(source.getHeight(), source.getWidth())) {
            return;
        }

        int width = source.getWidth();
        int height = source.getHeight();
                                                                
        double kernelCoefficient = 1.0 / ((2.0 * (double) kernelRadius) + 1.0);

        // Go through each row first, on the source itself
        for (int i = 0; i < height; ++i) {
            
            Color fcolor = new Color(target.getRGB(0, i));
            Color lastColor = new Color(target.getRGB(width - 1, i));
            int sumR = fcolor.getRed() * (kernelRadius + 1);
            int sumG = fcolor.getGreen() * (kernelRadius + 1);
            int sumB = fcolor.getBlue() * (kernelRadius + 1);

            for (int j = 0; j < kernelRadius; ++j) {
                Color color = new Color(target.getRGB(j, i));
                sumR += color.getRed();
                sumG += color.getGreen();
                sumB += color.getBlue();
            }            

            int ti = 0;
            int li = ti;
            int ri = ti + kernelRadius;

            for (int j = 0; j <= kernelRadius; ++j) {

                Color color = new Color(target.getRGB(ri, i));
                sumR += (color.getRed() - fcolor.getRed());
                sumG += (color.getGreen() - fcolor.getGreen());
                sumB += (color.getBlue()- fcolor.getBlue());
                ++ri;
                int finalR = (int) Math.round((double) sumR * kernelCoefficient);
                int finalG = (int) Math.round((double) sumG * kernelCoefficient);
                int finalB = (int) Math.round((double) sumB * kernelCoefficient);
                Color finalColor = new Color(finalR, finalG, finalB);
                source.setRGB(ti, i, finalColor.getRGB());
                ++ti;
            }

            for (int j = kernelRadius + 1; j < width - kernelRadius; ++j) {

                Color color = new Color(target.getRGB(ri, i));
                Color lcolor = new Color(target.getRGB(li, i));
                ++li;
                sumR += (color.getRed() - lcolor.getRed());
                sumG += (color.getGreen() - lcolor.getGreen());
                sumB += (color.getBlue()- lcolor.getBlue());
                ++ri;
                int finalR = (int) Math.round((double) sumR * kernelCoefficient);
                int finalG = (int) Math.round((double) sumG * kernelCoefficient);
                int finalB = (int) Math.round((double) sumB * kernelCoefficient);
                Color finalColor = new Color(finalR, finalG, finalB);
                source.setRGB(ti, i, finalColor.getRGB());
                ++ti;
            }

            for (int j = width - kernelRadius; j < width; ++j) {

                Color lcolor = new Color(target.getRGB(li, i));
                ++li;
                sumR += (lastColor.getRed() - lcolor.getRed());
                sumG += (lastColor.getGreen() - lcolor.getGreen());
                sumB += (lastColor.getBlue()- lcolor.getBlue());
                
                int finalR = (int) Math.round((double) sumR * kernelCoefficient);
                int finalG = (int) Math.round((double) sumG * kernelCoefficient);
                int finalB = (int) Math.round((double) sumB * kernelCoefficient);
                Color finalColor = new Color(finalR, finalG, finalB);
                source.setRGB(ti, i, finalColor.getRGB());
                ++ti;
            }
        }

        // Go through each column, on the target
        for (int i = 0; i < width; ++i) {

            Color fcolor = new Color(source.getRGB(i, 0));
            Color lastColor = new Color(source.getRGB(i, height - 1));
            int sumR =  fcolor.getRed() * (kernelRadius + 1);
            int sumG = fcolor.getGreen() * (kernelRadius + 1);
            int sumB = fcolor.getBlue() * (kernelRadius + 1);

            for (int j = 0; j < kernelRadius; ++j) {
                Color color = new Color(source.getRGB(i, j));
                sumR += color.getRed();
                sumG += color.getGreen();
                sumB += color.getBlue();
            }

            int ti = 0;
            int li = ti;
            int ri = ti + kernelRadius;

            for (int j = 0; j <= kernelRadius; ++j) {

                Color color = new Color(source.getRGB(i, ri));
                sumR += (color.getRed() - fcolor.getRed());
                sumG += (color.getGreen() - fcolor.getGreen());
                sumB += (color.getBlue()- fcolor.getBlue());
                ++ri;
                int finalR = (int) Math.round((double) sumR * kernelCoefficient);
                int finalG = (int) Math.round((double) sumG * kernelCoefficient);
                int finalB = (int) Math.round((double) sumB * kernelCoefficient);
                Color finalColor = new Color(finalR, finalG, finalB);
                target.setRGB(i, ti, finalColor.getRGB());
                ++ti;
            }

            for (int j = kernelRadius + 1; j < height - kernelRadius; ++j) {

                Color color = new Color(source.getRGB(i, ri));
                Color lcolor = new Color(source.getRGB(i, li));
                ++li;
                sumR += (color.getRed() - lcolor.getRed());
                sumG += (color.getGreen() - lcolor.getGreen());
                sumB += (color.getBlue()- lcolor.getBlue());
                ++ri;
                int finalR = (int) Math.round((double) sumR * kernelCoefficient);
                int finalG = (int) Math.round((double) sumG * kernelCoefficient);
                int finalB = (int) Math.round((double) sumB * kernelCoefficient);
                Color finalColor = new Color(finalR, finalG, finalB);
                target.setRGB(i, ti, finalColor.getRGB());
                ++ti;
            }

            for (int j = height - kernelRadius; j < height; ++j) {

                Color lcolor = new Color(source.getRGB(i, li));
                ++li;
                sumR += (lastColor.getRed() - lcolor.getRed());
                sumG += (lastColor.getGreen() - lcolor.getGreen());
                sumB += (lastColor.getBlue()- lcolor.getBlue());
                
                int finalR = (int) Math.round((double) sumR * kernelCoefficient);
                int finalG = (int) Math.round((double) sumG * kernelCoefficient);
                int finalB = (int) Math.round((double) sumB * kernelCoefficient);
                Color finalColor = new Color(finalR, finalG, finalB);
                target.setRGB(i, ti, finalColor.getRGB());
                ++ti;
            }
        }
    }

    /**
     * Uses box blur to achieve the effect of gaussian blur.
     * @param image     The input image
     * @param intensity Integer in the range
     *                  [1, min({@code image.height} - 1, {@code image.width} - 1)]
     * @return          The blurred image
     */
    public static BufferedImage gaussianBlur(BufferedImage image, int intensity) {

        // Incorrect value of intensity
        if (intensity < 0 || intensity >= Math.min(image.getWidth(), image.getHeight())) {
            return null;
        }

        BufferedImage source = Helpers.deepCopy(image);
        BufferedImage target = Helpers.deepCopy(image);

        // Apply box blur 3 times, incrementally
        boxBlur(source, target, intensity);
        boxBlur(target, source, intensity + 1);
        boxBlur(source, target, intensity + 2);

        return target;
    }

    /**
     * Takes an image and returns a posterized version of it by reducing 
     * its distinct pixels.  
     * @param image The input image
     * @return      The posterized image
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage posterize(BufferedImage image) 
                            throws ArrayIndexOutOfBoundsException {
        
        // We apply the Helpers.reducePixel() method on R,G,B value of every pixel.

        BufferedImage result = Helpers.deepCopy(image);
        int width = image.getWidth();
        int height = image.getHeight();
    
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {

                // get the color
                Color originalColor = new Color(result.getRGB(j, i));

                // reduce the pixels
                int red = Helpers.reducePixel(originalColor.getRed());
                int green = Helpers.reducePixel(originalColor.getGreen());
                int blue = Helpers.reducePixel(originalColor.getBlue());

                // apply the color
                Color finalColor = new Color(red, green, blue);
                result.setRGB(j, i, finalColor.getRGB());
            }
        }

        return result;
    }
    
    /**
     * Takes an image and returns a pixelated version of it.
     * @param image         The input image
     * @param pixelWidth    An integer in the range 
     *                      [1, min({@code height} - 1, {@code width} - 1)]
     * @return              The pixelated image
     * @throws ArrayIndexOutOfBoundsException
     */
    public static BufferedImage pixelate(BufferedImage image, int pixelWidth)
                                            throws ArrayIndexOutOfBoundsException {

        // A square window of side length pixelWidth slides through the image,
        // jumping at a length of pixelWidth after each iteration. In each slide,
        // it sets the R,G,B values of every pixel to the average values in that window.

        BufferedImage result = Helpers.deepCopy(image);
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < height; i += pixelWidth) {
            for (int j = 0; j < width; j += pixelWidth) {

                int avgRed = 0;
                int avgGreen = 0;
                int avgBlue = 0;

                int totalRed = 0;
                int totalGreen = 0;
                int totalBlue = 0;

                int count = 0;

                // Traverse and add the pixel values
                for (int y = i; y < i + pixelWidth && y < height; ++y) {
                    for (int x = j; x < j + pixelWidth && x < width; ++x) {

                        Color color = new Color(result.getRGB(x, y));

                        totalRed += color.getRed();
                        totalGreen += color.getGreen();
                        totalBlue += color.getBlue();
                        ++count;
                    }
                }

                // Compute the average
                avgRed = totalRed / count;
                avgGreen = totalGreen / count;
                avgBlue = totalBlue / count;
                
                // Get the color made using the R,G,B values
                Color finalColor = new Color(avgRed, avgGreen, avgBlue);

                // Set all pixels in the submatrix to finalColor
                for (int y = i; y < i + pixelWidth && y < height; ++y) {
                    for (int x = j; x < j + pixelWidth && x < width; ++x) {

                        result.setRGB(x, y, finalColor.getRGB());
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * Takes an image and returns a sharpened version of it with given intensity. 
     * @param image     The input image
     * @param intensity Integer indicating the sharpness required
     * @return          The sharpened image
     */
    public static BufferedImage sharpen(BufferedImage image, int intensity) {

     // The intensity parameter is used to produce a 3x3 kernel
     // which will be convolved with the image. The process is similar to
     // blurring an image using box blur just once. Since the kernel 
     // is just 3x3, the total steps for convolutionResult calculation in each 
     // iteration will always be 9. Hence, this kernel is not made separable and 
     // works in O(height * width), similar to box blur with a separable kernel 
     // and varying kernelRadius. The intensity can theoretically range from 
     // [0, infinity). However, in practice, after a threshold, the higher values 
     // would become pointless to use due to results that would not please the
     // human eye. That threshold would depend upon how blurred the input image is.

        BufferedImage result = Helpers.deepCopy(image);
        int width = image.getWidth();
        int height = image.getHeight();

        // Get the kernel with applied intensity
        int[][] kernel = Kernels.getSharpenKernel(intensity);

        for (int i = 1; i < height - 1; ++i) {
            for (int j = 1; j < width - 1; ++j) {

                // Initialise to store the result of convolution
                int[] convolutionResult = {0, 0, 0};

                // Convolve
                for (int x = -1; x <= 1; ++x) {
                    for (int y = -1; y <= 1; ++y) {
                        
                        Color color = new Color(image.getRGB(j + x, i + y));
                        convolutionResult[0] += kernel[x + 1][y + 1] * color.getRed();
                        convolutionResult[1] += kernel[x + 1][y + 1] * color.getGreen();        
                        convolutionResult[2] += kernel[x + 1][y + 1] * color.getBlue();
                    }
                }

                // Truncate the values if they went out of bounds and store them
                int finalRed = Helpers.truncateIfNeeded(convolutionResult[0]);
                int finalGreen = Helpers.truncateIfNeeded(convolutionResult[1]);
                int finalBlue = Helpers.truncateIfNeeded(convolutionResult[2]);

                // Produce the color
                Color finalColor = new Color(finalRed, finalGreen, finalBlue);

                // Set the final color
                result.setRGB(j, i, finalColor.getRGB());
            }
        }

        return result;
    }
}
