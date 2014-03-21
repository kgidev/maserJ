/**
 * 
 */
package view;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.MyImage;

public class GridImageTest {

	GridImage gridImg;

	/**
	 * Test method for {@link view.GridImage#GridImage(int, int, int)}.
	 */
	@Test
	public final void testGridImage() {

		// MUT
		int imgDim=600;
		gridImg = new GridImage(imgDim, imgDim);

		TextDataImageTest.saveImage2Tmp(gridImg, false,"testGridImage");
		int expectedValue=imgDim*4;
		// the test
		int returnValue = MyImage.countColorPixel(gridImg, gridImg.fgColor);
		// System.out.println("countColorPixel: "+returnValue);
		assertTrue("amount of Color Pixels: ", returnValue>expectedValue);
		
	}


}
