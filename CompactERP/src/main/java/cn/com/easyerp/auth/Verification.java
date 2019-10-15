package cn.com.easyerp.auth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

public class Verification {
    public static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static Random random;

    public static String generateVerifyCode(final int verifySize) {
        return generateVerifyCode(verifySize, "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");
    }

    public static String generateVerifyCode(final int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        }
        final int codesLen = sources.length();
        final Random rand = new Random(System.currentTimeMillis());
        final StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; ++i) {
            verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
        }
        return verifyCode.toString();
    }

    public static String outputVerifyImage(final int w, final int h, final File outputFile, final int verifySize)
            throws IOException {
        final String verifyCode = generateVerifyCode(verifySize);
        outputImage(w, h, outputFile, verifyCode);
        return verifyCode;
    }

    public static String outputVerifyImage(final int w, final int h, final OutputStream os, final int verifySize)
            throws IOException {
        final String verifyCode = generateVerifyCode(verifySize);
        outputImage(w, h, os, verifyCode);
        return verifyCode;
    }

    public static void outputImage(final int w, final int h, final File outputFile, final String code)
            throws IOException {
        if (outputFile == null) {
            return;
        }
        final File dir = outputFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            outputFile.createNewFile();
            final FileOutputStream fos = new FileOutputStream(outputFile);
            outputImage(w, h, fos, code);
            fos.close();
        } catch (IOException e) {
            throw e;
        }
    }

    public static void outputImage(final int w, final int h, final OutputStream os, final String code)
            throws IOException {
        final int verifySize = code.length();
        final BufferedImage image = new BufferedImage(w, h, 1);
        final Random rand = new Random();
        final Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Color[] colors = new Color[5];
        final Color[] colorSpaces = { Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA,
                Color.ORANGE, Color.PINK, Color.YELLOW };
        final float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);
        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, w, h);
        final Color c = getRandColor(200, 250);
        g2.setColor(c);
        g2.fillRect(0, 2, w, h - 4);
        final Random random = new Random();
        g2.setColor(getRandColor(160, 200));
        for (int j = 0; j < 20; ++j) {
            final int x = random.nextInt(w - 1);
            final int y = random.nextInt(h - 1);
            final int xl = random.nextInt(6) + 1;
            final int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }
        final float yawpRate = 0.05f;
        for (int area = (int) (yawpRate * w * h), k = 0; k < area; ++k) {
            final int x2 = random.nextInt(w);
            final int y2 = random.nextInt(h);
            final int rgb = getRandomIntColor();
            image.setRGB(x2, y2, rgb);
        }
        shear(g2, w, h, c);
        g2.setColor(getRandColor(100, 160));
        final int fontSize = h - 4;
        final Font font = new Font("Algerian", 2, fontSize);
        g2.setFont(font);
        final char[] chars = code.toCharArray();
        for (int l = 0; l < verifySize; ++l) {
            final AffineTransform affine = new AffineTransform();
            affine.setToRotation(0.7853981633974483 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1),
                    w / verifySize * l + fontSize / 2, h / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, l, 1, (w - 10) / verifySize * l + 5, h / 2 + fontSize / 2 - 10);
        }
        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        final int r = fc + Verification.random.nextInt(bc - fc);
        final int g = fc + Verification.random.nextInt(bc - fc);
        final int b = fc + Verification.random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        final int[] rgb = getRandomRgb();
        int color = 0;
        for (final int c : rgb) {
            color <<= 8;
            color |= c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        final int[] rgb = new int[3];
        for (int i = 0; i < 3; ++i) {
            rgb[i] = Verification.random.nextInt(255);
        }
        return rgb;
    }

    private static void shear(final Graphics g, final int w1, final int h1, final Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(final Graphics g, final int w1, final int h1, final Color color) {
        final int period = Verification.random.nextInt(2);
        final boolean borderGap = true;
        final int frames = 1;
        final int phase = Verification.random.nextInt(2);
        for (int i = 0; i < h1; ++i) {
            final double d = (period >> 1) * Math.sin(i / (double) period + 6.283185307179586 * phase / frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
    }

    private static void shearY(final Graphics g, final int w1, final int h1, final Color color) {
        final int period = Verification.random.nextInt(40) + 10;
        final boolean borderGap = true;
        final int frames = 20;
        final int phase = 7;
        for (int i = 0; i < w1; ++i) {
            final double d = (period >> 1) * Math.sin(i / (double) period + 6.283185307179586 * phase / frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }
        }
    }

    static {
        Verification.random = new Random();
    }
}
