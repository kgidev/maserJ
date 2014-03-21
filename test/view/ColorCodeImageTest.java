/**
 * 
 */
package view;

import static common.TEST.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.MyImage;


public class ColorCodeImageTest {
	ColorCodeImage ccImg;

	
	/**
	 * Test method for {@link view.ColorCodeImage#ColorCodeImage(int, int, int)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testColorCodeImage() {

		// MUT
		ccImg = new ColorCodeImage(50, 500);

		TextDataImageTest.saveImage2Tmp(ccImg, false,"testColorCodeImage()");
		// the test
		int returnValue = MyImage.countColorPixel(ccImg, Color.RED);
		// System.out.println("countColorPixel: "+returnValue);
		assertEquals("amount of Color Pixels: ", 0, returnValue);
		returnValue = MyImage.countColorPixel(ccImg, Color.GREEN);
		// System.out.println("countColorPixel: "+returnValue);
		assertTrue("amount of Color Pixels: ", returnValue > 40);
		returnValue = MyImage.countColorPixel(ccImg, Color.BLUE);
		// System.out.println("countColorPixel: "+returnValue);
		assertTrue("amount of Color Pixels: ", returnValue > 40);
		returnValue = MyImage.countColorPixel(ccImg, Color.BLACK);
		// System.out.println("countColorPixel: "+returnValue);
		assertTrue("amount of Color Pixels: ", returnValue > 100);
	}

	/**
	 * Test method for {@link view.ColorCodeImage#createScale()}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testCreateScale() {
		ccImg = new ColorCodeImage(500, 50);
		// MUT
		BufferedImage scaleImg = ccImg.createScale();
		Color scaleColor = Color.BLACK;
		int returnValue = MyImage.countColorPixel(scaleImg, scaleColor);
		 TextDataImageTest.saveImage2Tmp(scaleImg, false, this.getClass().getName()+"_testCreateScale()");
		// the test
		int expectedValue =  scaleImg.getWidth()*2;
		assertTrue("amount of Color Pixels: ", returnValue > expectedValue);

	}

}
