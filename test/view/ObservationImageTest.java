/**
 * 
 */
package view;

import static common.TEST.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import controller.MaserJ;

import tools.MyImage;

public class ObservationImageTest {
	ObservationImage obsImg;
	
	/**
	 * Test method for {@link view.ObservationImage#ObservationImage(int, int)}.
	 */
	//@Ignore
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testObservationImageConstructor0() {
//		 MUT
		int w=800;
		int h=600;
		obsImg = new ObservationImage(w, h);
//		 the test  // TODO
		TextDataImageTest.saveImage2Tmp(obsImg, false,"testObservationImageConstructor0");
		assertEquals("width: ", w,obsImg.getWidth());
		assertEquals("height: ", h,obsImg.getHeight());
	}

	/**
	 * Test method for {@link view.ObservationImage#ObservationImage(int, int, java.awt.image.BufferedImage, java.awt.image.BufferedImage, java.awt.image.BufferedImage, java.awt.image.BufferedImage, java.awt.image.BufferedImage)}.
	 * @throws IOException 
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testObservationImageConstructor1() throws IOException {
		 String placeHolderImgPath=
			FilenameUtils.separatorsToSystem("lib/image/nopictureMid.png");
		 int gridSize=600;
		 BufferedImage gridImg=new BufferedImage(gridSize,gridSize,MaserJ.BIMG_TYPE);
		 Color gridColor=Color.BLUE;
		 MyImage.setBackgroundColor(gridImg, gridColor);
		 
		 int colorCodeImgColorSize=50;
		 BufferedImage colorCodeImg=new BufferedImage(colorCodeImgColorSize,
				 colorCodeImgColorSize,MaserJ.BIMG_TYPE);
		 Color colorCodeImgColor=Color.RED;
		 MyImage.setBackgroundColor(colorCodeImg, colorCodeImgColor);
		
		 int celestialObjectTextImgSize=150;
		 BufferedImage celestialObjectTextImg=new BufferedImage(
				 celestialObjectTextImgSize,celestialObjectTextImgSize,
				 MaserJ.BIMG_TYPE);
		 Color celestialObjectTextImgColor=Color.CYAN;
		 MyImage.setBackgroundColor(celestialObjectTextImg, celestialObjectTextImgColor);

		 int observationTextImgSize=100;
		 BufferedImage observationTextImg=new BufferedImage(
				 observationTextImgSize,observationTextImgSize,
				 MaserJ.BIMG_TYPE);
		 Color observationTextImgColor=Color.GREEN;
		 MyImage.setBackgroundColor(observationTextImg, observationTextImgColor);

		 int maserImgSize=500;
		 BufferedImage maserImg=new BufferedImage(
				 maserImgSize,maserImgSize,
				 MaserJ.BIMG_TYPE);
		 Color maserImgColor=Color.magenta;
		 MyImage.setBackgroundColor(maserImg, maserImgColor);
		
		 // MUT
		int w=800;
		int h=600;
		obsImg = new ObservationImage(w, h, gridImg, colorCodeImg,
				celestialObjectTextImg,observationTextImg, maserImg);

		TextDataImageTest.saveImage2Tmp(obsImg, false,"testObservationImageConstructor1");
		//the tests
		int returnColor=obsImg.getRGB(obsImg.gridImgX, obsImg.gridImgY);
		assertEquals("GridImg Color: ", gridColor.getRGB(),returnColor);

		returnColor=obsImg.getRGB(obsImg.colorCodeImgX, obsImg.colorCodeImgY);
		assertEquals("GridImg Color: ", colorCodeImgColor.getRGB(),returnColor);

		returnColor=obsImg.getRGB(obsImg.celestialObjectTextImgX, obsImg.celestialObjectTextImgY);
		assertEquals("GridImg Color: ", celestialObjectTextImgColor.getRGB(),returnColor);

		returnColor=obsImg.getRGB(obsImg.observationTextImgX, obsImg.observationTextImgY);
		assertEquals("GridImg Color: ", observationTextImgColor.getRGB(),returnColor);
		
		returnColor=obsImg.getRGB(obsImg.maserImgX, obsImg.maserImgY);
		assertEquals("GridImg Color: ", maserImgColor.getRGB(),returnColor);
	}
}
