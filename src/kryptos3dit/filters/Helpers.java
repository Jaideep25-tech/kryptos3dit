package kryptos3dit.filters;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.RasterFormatException;
import java.lang.IllegalArgumentException;

/** 
 * Contains utility methods used by {@code Filters.java}
 * @author Jaideep
 */
final class Helpers {

    /**
     * Makes a deep copy of the input image to which filters are applied.
     * @param image The input image
     * @return      A new instance of BufferedImage, which is made using the input
     * @throws RasterFormatException
     * @throws IllegalArgumentException
     */
    static BufferedImage deepCopy(BufferedImage image) 
            throws RasterFormatException, IllegalArgumentException {

        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);

        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Convert degree to radian
     * @param degreeAngle An angle in degrees
     * @return            The angle in radians  
     */
    static double toRadians(double degreeAngle) {

        double result = (degreeAngle * Math.PI) / 180.0;
        return result;
    }

    /**
     * Convert raster points to cartesian points
     * @param rasterX   x co-ordinate in raster plane, which is to be converted
     * @param rasterY   y co-ordinate in raster plane, which is to be converted
     * @param centreX   x co-ordinate of the given raster plane's centre
     * @param centreY   y co-ordinate of the given raster plane's centre
     * @return          An array of size 2 where first value is the cartesian X co-ordinate and 
     *                  second value is the cartesian Y co-ordinate
     */
    static int[] toCartesian(int rasterX, int rasterY, int centreX, int centreY) {

        int points[] = new int[2];
        points[0] = rasterX - centreX;
        points[1] = centreY - rasterY;

        return points;
    }

    /**
     * Convert a cartesian co-ordinate into its polar form
     * @param cartesianX 
     * @param cartesianY
     * @return An array of size 2 where first value is the distance of the point from origin
     *         and second value is the polar angle 
     */
    static double[] toPolar(int cartesianX, int cartesianY) {

        double data[] = new double[2];
        data[0] = Math.sqrt((cartesianX * cartesianX) + (cartesianY * cartesianY));

        if (cartesianX == 0) {
            if (cartesianY < 0) {
                data[1] = 1.5 * Math.PI;
            }
            else {
                data[1] = 0.5 * Math.PI;
            }
        }
        else {
            data[1] = Math.atan2((double) cartesianY, (double) cartesianX);
        }

        return data;
    }

    /**
     * Takes polarData and returns the cartesian X and cartesian Y value
     * @param polarData An array double[2] where first value is the distance of the point
     *                  from origin and the second value is the polar angle
     * @return An array [cartesianX, cartesianY]
     */
    static double[] toCartesian(double[] polarData) {

        double[] points = new double[2];
        points[0] = polarData[0] * Math.cos(polarData[1]);
        points[1] = polarData[0] * Math.sin(polarData[1]);

        return points;
    }

    /**
     * Cartesian to raster
     * @param cartesianPoints An array [cartesianX, cartesianY]
     * @param centreX         x co-ordinate of the given raster plane's centre
     * @param centreY         y co-ordinate of the given raster plane's centre
     * @return                An array [rasterX, rasterY]
     */
    static double[] toRaster(double[] cartesianPoints, int centreX, int centreY) {

        double[] points = new double[2];
        points[0] = cartesianPoints[0] + (double)centreX;
        points[1] = (double)centreY - cartesianPoints[1];

        return points;
    }

    /**
     * Take the raster points of type double[] and returns the floored values
     * @param rasterPoints An array of type double {rasterX, rasterY}
     * @return             An array of type int {rasterX, rasterY}
     */
    static int[] floorPoints(double[] rasterPoints) {

        int[] floored = new int[2];
        floored[0] = (int)Math.floor(rasterPoints[0]);
        floored[1] = (int)Math.floor(rasterPoints[1]);

        return floored;
    }

    /**
     * Take the raster points of type double[] and returns the ceiled values
     * @param rasterPoints An array of type double {rasterX, rasterY}
     * @return             An array of type int {rasterX, rasterY}
     */
    static int[] ceilPoints(double[] rasterPoints) {

        int[] ceiled = new int[2];
        ceiled[0] = (int)Math.ceil(rasterPoints[0]);
        ceiled[1] = (int)Math.ceil(rasterPoints[1]);

        return ceiled;
    }

    /**
     * Check if a given index is out of bounds w.r.t. the given array's length
     * @param givenIndex  
     * @param length
     * @return      {@code True} if given index is out of bounds
     */
    static boolean isOutOfBounds(int givenIndex, int length) {

        return (givenIndex < 0 || givenIndex >= length);
    }

    /**
     * Linear interpolation, {@code f(t) = (1 - t) * p1 + (t * p2)}
     * where p1 and p2 are two points in-between which we need to
     * find a new point which is at 't' times the distance between p1 and p2
     * @param left  Any point
     * @param delta A value in the range [0, 1]
     * @param right Any point greater than left on the real number line
     * @return      The result of interpolation
     */
    static double linearInterpolation(double left, double delta, double right) {

        // Handle an invalid case, could return -1 too but that will break the methods of Filters.java
        if (delta < 0 || delta > 1) {
            return 0;
        }

        double result = (1 - delta) * left + (delta * right);

        return result;
    }

    /**
     * Truncate a pixel value if it is outside the range of [0, 255]
     * @param pixel
     * @return A truncated pixel value
     */
    static int truncateIfNeeded(int pixel) {

        if (pixel < 0) {
            return 0;
        }

        if (pixel > 255) {
            return 255;
        }

        return pixel;
    }

    /**
     * Perform bilinear interpolation on given 4 points
     * @param topLeft       A point of type double
     * @param topRight      A point of type double
     * @param bottomLeft    A point of type double
     * @param bottomRight   A point of type double
     * @param deltaX        A value in the range [0, 1], for horizontal linear interpolation
     * @param deltaY        A value in the range [0, 1], for vertical linear interpolation
     * @return              The interpolated value
     */
    static int bilinearInterpolation(Color topLeft, Color topRight, 
                                Color bottomLeft, Color bottomRight, 
                                double deltaX, double deltaY) {

        // Horizontal                            
        double topR = linearInterpolation(topLeft.getRed(), deltaX, topRight.getRed());
        double topG = linearInterpolation(topLeft.getGreen(), deltaX, topRight.getGreen());
        double topB = linearInterpolation(topLeft.getBlue(), deltaX, topRight.getBlue());

        // Vertical
        double bottomR = linearInterpolation(bottomLeft.getRed(), deltaX, bottomRight.getRed());
        double bottomG = linearInterpolation(bottomLeft.getGreen(), deltaX, bottomRight.getGreen());
        double bottomB = linearInterpolation(bottomLeft.getBlue(), deltaX, bottomRight.getBlue());

        // Compute the R, G, B values
        int red = (int) Math.round(linearInterpolation(topR, deltaY, bottomR));
        int green = (int) Math.round(linearInterpolation(topG, deltaY, bottomG));
        int blue = (int) Math.round(linearInterpolation(topB, deltaY, bottomB));


        red = truncateIfNeeded(red);
        green = truncateIfNeeded(green);
        blue = truncateIfNeeded(blue);

        return new Color(red, green, blue).getRGB();
    }

    /**
     * Changes the intensity of all pixel values of an image w.r.t the dial value
     * @param image The input image, the operation is performed on the input itself
     * @param dial  A value in the range [-1.0, 1.0]
     * @throws ArrayIndexOutOfBoundsException
     */
    static void lightDial(BufferedImage image, double dial) throws ArrayIndexOutOfBoundsException {

        double limit = (dial > 0.0) ? 255.0 : 0.0;

        for (int i = 0; i < image.getHeight(); ++i) {

            for (int j = 0; j < image.getWidth(); ++j) {

                Color color = new Color(image.getRGB(j, i));
                int newR = 0;
                int newG = 0;
                int newB = 0;

                if (dial > 0.0) {
                    newR = (int) linearInterpolation(color.getRed(), dial, limit);
                    newG = (int) linearInterpolation(color.getGreen(), dial, limit);
                    newB = (int) linearInterpolation(color.getBlue(), dial, limit);
                }            
                else {
                    dial = -1.0 * dial;
                    newR = (int) linearInterpolation(limit, dial, color.getRed());
                    newG = (int) linearInterpolation(limit, dial, color.getGreen());
                    newB = (int) linearInterpolation(limit, dial, color.getBlue());
                }    

                Color newColor = new Color(newR, newG, newB);
                image.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    /**
     * Takes a pixel value and reduces it to one of the 4 distinct values.
     * If the value is invalid, the method returns {@code 255}.
     * @param pixel A pixel value
     * @return      A reduced pixel value
     */
    static int reducePixel(int pixel) {

        if (pixel < 64) {
            return 0;
        }

        if (pixel >= 64 && pixel < 128) {
            return 64;
        }

        if (pixel >= 128 && pixel < 192) {
            return 128;
        }

        if (pixel >= 192 && pixel < 255) {
            return 192;
        }

        return 255;
    }
};
