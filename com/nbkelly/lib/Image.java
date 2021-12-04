package com.nbkelly.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
 
import javax.imageio.ImageIO;

public class Image {
    private int width;
    private int height;

    BufferedImage bufferedImage;
    Graphics2D g2d;

    public static Color C1 = new Color(0x10FFCB);
    public static Color C2 = new Color(0xB5F8FE);
    public static Color C3 = new Color(0xFBD87F);
    public static Color C4 = new Color(0xFCE4D8);
    public static Color C5 = new Color(0x05668D);

    public static Color W1 = new Color(0xFFFFFF);
    public static Color W2 = new Color(0xCCCCCC);
    
    public static Font F1 = new Font("Consolas", Font.PLAIN, 10);
    public static Integer F1_SPACE = 12;
    
    /**
     * Creates a simple image with the given dimensions
     */
    public Image(int width, int height) {
	bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	g2d = bufferedImage.createGraphics();

	this.width = width;
	this.height = height;
	
	g2d.setColor(C5);
	g2d.fillRect(0, 0, width, height);

	g2d.dispose();
    }

    /**
     * Fills a rectangle in this simple image
     */
    public void rect(Color c, int x1, int y1, int width, int height) {
	g2d = bufferedImage.createGraphics();
	g2d.setColor(c);
	g2d.fillRect(x1, y1, width, height);
	g2d.dispose();
    }
    
    public void line(Color c, int x1, int y1, int x2, int y2) {
	g2d = bufferedImage.createGraphics();
	g2d.setColor(c);
	g2d.drawLine(x1, y1, x2, y2);
	g2d.dispose();
    }

    public void line(Color c, int x1, int y1, int x2, int y2, int thickness) {
	g2d = bufferedImage.createGraphics();
	g2d.setColor(c);
	for(int i = 0; i < thickness; i++) {
	    g2d.drawLine(x1, y1+i, x2, y2+i);	    
	    g2d.drawLine(x1, y1-i, x2, y2-i);
	    g2d.drawLine(x1-i, y1, x2-i, y2);
	    g2d.drawLine(x1+i, y1, x2+i, y2);
	}
	g2d.dispose();
    }

    public void text(Color c, Font f, int x1, int y1, String s) {
	g2d = bufferedImage.createGraphics();
	g2d.setColor(c);
	g2d.setFont(f);
	g2d.drawString(s, x1, y1);
	g2d.dispose();
    }

    /**
     * Saves this simple image as a png file with the given filename
     */
    public void savePNG(String fileName) throws Exception {
	File file = new File(fileName);
        ImageIO.write(bufferedImage, "png", file);
    }
}
