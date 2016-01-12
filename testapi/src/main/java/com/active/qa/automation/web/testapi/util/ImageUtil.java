package com.active.qa.automation.web.testapi.util;


import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import com.active.qa.automation.web.testapi.ItemNotFoundException;

/**
 * Wrap the methods for manipulating images
 * Created by tchen on 1/11/2016.
 */
public class ImageUtil {
    private static AutomationLogger logger = AutomationLogger.getInstance();

    /**
     * This method is used to compare two images one pixel by one pixel
     *
     * @param path
     *            Template File Path
     * @param compareImage
     *            Compared Image
     * @param resultPath
     *            Compare result path
     * @throws InterruptedException
     */
    public boolean processImage(String path, String compareImage,
                                String resultPath) throws InterruptedException {
        Image templateImage = null;
        Image image2 = null;
        File file = new File(path);
        File compFile = new File(compareImage);
        String mayDifferTemplate = "";
        boolean isCorrect = true;
        if (!compFile.exists()) {
            logger.error(compFile + " is not exists!");
            throw new ItemNotFoundException(compFile + " not found");
        }
        if (file.exists() && file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equalsIgnoreCase(compFile.getName())) {
                    templateImage = Toolkit.getDefaultToolkit().getImage(
                            files[i].getAbsolutePath());
                    image2 = Toolkit.getDefaultToolkit().getImage(
                            compFile.getAbsolutePath());
                    mayDifferTemplate = files[i].getAbsolutePath();
                    break;
                }
            }
        } else {
            logger.error(path + " template is not exists!");
            throw new ItemNotFoundException(path + " not found");
        }
        if (null == templateImage) {
            logger.error(compareImage + " template is not exists!");
            throw new ItemNotFoundException(compareImage + " not found");
        }
        PixelGrabber grabber = new PixelGrabber(templateImage, 0, 0, -1, -1,
                false);
        PixelGrabber grabber2 = new PixelGrabber(image2, 0, 0, -1, -1, false);
        if (grabber.grabPixels() && grabber2.grabPixels()) {
            int width = grabber.getWidth();
            int height = grabber.getHeight();
            int width2 = grabber2.getWidth();
            int height2 = grabber2.getHeight();
            if (width != width2 || height != height2) {
                compareByImageMagick(mayDifferTemplate, compareImage,
                        resultPath);
                logger.error(compareImage
                        + " is Not Correct,and the result is in " + resultPath);
                return !isCorrect;
            }

            if (grabber.getPixels() instanceof int[])// Image Type is Jpeg
            {
                int[] data = (int[]) grabber.getPixels();
                int[] data2 = (int[]) grabber2.getPixels();
                if (java.util.Arrays.equals(data, data2))
                    logger.info(compareImage + " is correct!");
                else {
                    compareByImageMagick(mayDifferTemplate, compareImage,
                            resultPath);
                    logger.error(compareImage
                            + " is Not Correct,and the result is in "
                            + resultPath);
                    return !isCorrect;
                }
            } else if (grabber.getPixels() instanceof byte[])// Image Type is
            // PNG
            {
                byte[] data = (byte[]) grabber.getPixels();
                byte[] data2 = (byte[]) grabber2.getPixels();
                if (java.util.Arrays.equals(data, data2))
                    logger.info(compareImage + " is correct!");
                else {
                    compareByImageMagick(mayDifferTemplate, compareImage,
                            resultPath);
                    logger.error(compareImage
                            + " is Not Correct,and the result is in "
                            + resultPath);
                    return !isCorrect;
                }

            }
        }
        return isCorrect;
    }

    /**
     * compare two images and produce a result image by ImageMagick tool
     *
     * @param template
     *            template image
     * @param image
     *            we want to verify
     * @param resultImage
     *            compare result
     */
    private void compareByImageMagick(String template, String image,
                                      String resultImage) {
        File file = new File(image);
        String command = "composite " + template + " " + image
                + " \\ -compose difference " + resultImage + "\\"
                + file.getName();
        try {
            Runtime.getRuntime().exec(command);
            logger.info(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

