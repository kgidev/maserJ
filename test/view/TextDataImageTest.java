/**
 * 
 */
package view;

import static org.junit.Assert.*;

import static common.TEST.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import model.CelestialObject;
import model.CelestialObjectTest;
import model.DBTable;
import model.DataBase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tools.MyImage;
import tools.MyMath;
import tools.ToolBox;


public class TextDataImageTest {
	static final File TMPDIR=SystemUtils.getJavaIoTmpDir();
	static DataBase dataBase;
	TextDataImage img;
	HashMap<String,Object> textData=new HashMap<String,Object>();
	int w =200;
	int h =200;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dataBase = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//dataBase.shutdownDB();
		dataBase=null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		img=null;
	}

	/**
	 * Test method for {@link view.TextDataImage#TextDataImage(int, int, int, java.util.HashMap)}.
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testTextDataImage() {
		
		textData.put("testKey0", "testValue0");
		
		//MUT
		img=new TextDataImage(w,h,textData,"TestHeader");
		//the test
		assertEquals("width:",w,img.getWidth());
		assertEquals("height:",h,img.getHeight());
		assertEquals("imgType:",BufferedImage.TYPE_INT_ARGB,img.getType());
		assertEquals("texData:",textData,img.textData);
//		saveImg
//		saveImage2Tmp(img,false);
	}

	/**
	 * Test method for {@link view.TextDataImage#TextDataImage(int, int, int, java.util.HashMap,String[])}.
	 * @throws Exception 
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testTextDataImageSort() throws Exception {
//		create CelestialObject-TextData
//		textOrder>keys
		textData.clear();
		textData.put("EPOCH", "EPOCHValue");
		textData.put("ra", 99.876);
		textData.put("NAME", "NameValue3");
		textData.put("dec", 53.88);
		textData.put("ra", 77.66);
		textData.put("test0", "test0Value3");
		textData.put("test", "testValue3");
	
		String[] textOrder={"NAME","RA","DEC","EPOCH","EPOCH","EPOCH","EPOCH"};
		
		//MUT
		img=new TextDataImage(w,h,textData,"TestHeader",textOrder);
		
		//textOrder<keys
		textData.clear();
		textData.put("instrument", "INSTRUMENTValue");
		textData.put("noise", 77.99);
		textData.put("name", "NameValue3");
		textData.put("interpolated", true);
		textData.put("ra", 9.99);
		textData.put("date", "test0Value3");
		textData.put("test", "testValue3");
		String[] textOrder1={"INSTRUMENT","DATE","NOISE","INTERPOLATED"};
		//MUT
		TextDataImage img1=new TextDataImage(w,h,textData,"TestHeader",textOrder1);
		//the tests 
		assertEquals("member Variable: ",textOrder,img.textOrder);
		assertEquals("member Variable: ",textOrder1,img1.textOrder);
//		saveImg
		saveImage2Tmp(img,false);
		saveImage2Tmp(img1,false);
	}
	
	
	/**
	 * Test method for {@link view.TextDataImage#drawTextData(java.util.HashMap)}.
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public final void testDrawTextData() {
		w=500;
		h=w;
		
		int charAmount=0;
		int mapsize=random.nextInt(10)+1;
		int keysize=random.nextInt(10)+1;
		int valuesize=random.nextInt(30);
		textData=randomTextDataMap( mapsize,  valuesize,  keysize); 
		img=new TextDataImage(500,500,textData,"TestHeader");
		charAmount = mapsize * (Math.max(keysize,valuesize) +
				Math.max(img.keySuffix.length(), img.valueIdent.length()));	
		
		//MUT
		
		img.drawTextData(textData);
//		System.out.println("font"+img.g2d.getFont());
		saveImage2Tmp(img,false, null);
		//the tests
		//foreGroundColor
		double minCharPixel=2;
		int cPixel=(img.getHeight()+img.getWidth())*2-4;	//Border
		cPixel+=minCharPixel*img.headerStr.length();		//HeaderString
		cPixel+=2*img.getWidth();								//HeaderBorder
		cPixel+=charAmount*minCharPixel;					//chars
//		System.out.println("cPixel: "+cPixel);
		int returnValue =MyImage.countColorPixel(img, img.fgColor);
//		System.out.println("MyImage.countColorPixel returnValue: "+returnValue);
		assertTrue("amount of Color Pixels:"+img.fgColor+": "+returnValue,
				returnValue>cPixel);
		assertEquals("height:",h,img.getHeight());
	
	}
	
	/**
	 * Test method for {@link view.TextDataImage#setWidth(java.util.HashMap)}.
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testSetWidth() {
		int mapsize=5;
		int keysize=10;
		int valuesize=20;
		//new instance CUT
		
		textData=randomTextDataMap( mapsize,  valuesize,  keysize);
		textData.put("12345678901", "123456789011234567890112345678899");
		img=new TextDataImage(w,h,textData,"TestHeader");
		int expectedWidth=img.fontMetrics.stringWidth(img.getMaxLengthStringFromMap(textData));
		
		//MUT
		img=new TextDataImage(w,h,textData,"TestHeader");
		img.setWidth(textData);
		int returnedWidth=img.getWidth();
//		saveImage2Tmp(img,false);
		//the test
		assertEquals("width:",expectedWidth,returnedWidth);
	}
	
//	/**
//	 * Test method for {@link view.TextDataImage#size2fit()}.
//	 */
//	@Test//(timeout = DEFAULT_TIMEOUT)
//	public final void testSize2fit() {
//		int mapsize=1;
//		int keysize=1;
//		int valuesize=1;
//		//new instance CUT
//		
//		textData=randomTextDataMap( mapsize,  valuesize,  keysize);
////		textData.put("12345678901", "123456789011234567890112345678899");
//
//		//MUT
//		img=new TextDataImage(w,h,textData, "TestHeader");
//		TextDataImage shrinkedimg=img.size2fit();
//		shrinkedimg.drawTextData(textData);
//		int expectedWidth=img.setWidth(textData);
//		int returnedWidth=shrinkedimg.getWidth();
//		int expectedHeight=img.setHeight(textData);
//		int returnedHeight=shrinkedimg.getHeight();
////		saveImage2Tmp(shrinkedimg,false, null);
//		if (expectedWidth<shrinkedimg.minWidth) expectedWidth=shrinkedimg.minWidth;
//		if (expectedHeight<shrinkedimg.minHeight) expectedHeight=shrinkedimg.minHeight;
//		
//		//the test
//		assertEquals("width:",expectedWidth,returnedWidth);
//		assertEquals("height:",expectedHeight,returnedHeight);
//	}
	
	/**
	 * Test method for {@link view.TextDataImage#setHeight(java.util.HashMap)}.
	 */
	@Test//(timeout = DEFAULT_TIMEOUT)
	public final void testSetHeight() {
		int mapsize=random.nextInt(7)+1;
		int keysize=10;
		int valuesize=20;
		//new instance CUT
		
		textData=randomTextDataMap( mapsize,  valuesize,  keysize);
		img=new TextDataImage(w,h,textData,"TestHeader");
		int expectedHeight=img.lineHeight*(mapsize+1)*2;
		
		//MUT
		img=new TextDataImage(w,expectedHeight,textData,"TestHeader");
		img.setHeight(textData);
		int returnedHeight=img.getHeight();
		//the test
		assertEquals("width:",expectedHeight,returnedHeight);
//		saveImg
		saveImage2Tmp(img,false, null);
		
	}

	/**
	 * Helper method
	 * @param img the Image to save 
	 * @param delete img after save or not
	 *  
	 */
	public boolean saveImage2Tmp(BufferedImage bi, boolean cleanup) {
		return saveImage2Tmp(bi, cleanup, null);
	}

		/**
		 * Helper method
		 * @param imgName TODO
		 * @param img the Image to save 
		 * @param delete img after save or not
		 *  
		 */
	static public boolean saveImage2Tmp(BufferedImage bi, boolean cleanup, String imgBaseName) {
			boolean success=false;
			String imagePath;
			File imageFile;
			BufferedImage image2save=bi;
//			ToolBox.getMethodName(this);
			String baseName=(imgBaseName==null)?"TextDataImageTest.saveImage2Tmp()":imgBaseName;
			try {
				imagePath = FilenameUtils.separatorsToSystem(TMPDIR.getCanonicalPath()
						+ "/"+baseName+".png");
				 imageFile = new File(imagePath);
				 MyImage.saveImage(image2save, imagePath);
				 success=true;
	//			 cleanup
				if (cleanup) FileUtils.forceDeleteOnExit(imageFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return success;
		}

		
		/**
		 * Helper method generate Random textDataMap
		 *  
		 */
		public HashMap<String,Object> randomTextDataMap(int mapsize, int valuesize, int keysize) 
		{
			HashMap<String,Object> randomMap=new HashMap<String,Object>(mapsize);
			for (int i = 0; i < mapsize; i++) {
				String key=RandomStringUtils.randomAscii(keysize);
				String value= RandomStringUtils.randomAscii(valuesize);
				randomMap.put(key,value);
			}
			return randomMap;
		}

	
		
		/**
		 * Test method for {@link view.TextDataImage#raSexagesimal(Double ra)}.
		 */
		@Test(timeout = DEFAULT_TIMEOUT)
		public final void testDecSexagesimal() {
			int testRuns=1000;
			for (int i = 0; i < testRuns; i++) {
				Double dec = (Double) MyMath.randomValueByRange(Double.class,
						-90.0, 90.0, '*');
//				System.out.println(ToolBox.getCurrentMethodName() + ": " + dec);
				
				Double sign = Math.signum(dec);
				
				Double wrkVal=new Double(Math.abs(dec));
				Integer expectedHours = wrkVal.intValue()*sign.intValue();
				
				wrkVal=wrkVal-Math.abs(expectedHours.doubleValue());
				Integer expectedMin = ((Double) (wrkVal*60)).intValue();
				
				wrkVal=wrkVal-(expectedMin).doubleValue()/60;			
				Integer expectedSecs =  ((Double)(wrkVal*3600)).intValue();
				
				wrkVal=wrkVal-((expectedSecs.doubleValue())/3600);
				Double expectedMas = Math.rint((wrkVal*3600000));
				
				//correction of seconds at expectedMas overflow 
				if (expectedMas>=1000) {
					expectedSecs+=1;
					expectedMas=0.0;
				}
			
				String expectedStr = expectedHours + ":" + expectedMin + ":"
										+expectedSecs+"."+expectedMas;
//				System.out.println(ToolBox.getCurrentMethodName() 
//						+ "expectedStr: "+ expectedStr);
				//MUT
				String returnStr = TextDataImage.decSexagesimal(dec);
				//the test
//				System.out.println(ToolBox.getCurrentMethodName() + ": "
//						+ returnStr);
				assertEquals("degrees:", expectedHours, 
						new Integer(returnStr.split(":")[0]));
				assertEquals("minutes:", expectedMin, new Integer(returnStr
						.split(":")[1]));
				assertEquals("seconds:", expectedSecs, new Integer(returnStr
						.split(":")[2].split("\\.")[0]));
				assertEquals("milliarcseconds:", expectedMas.intValue(), new Integer(returnStr
						.split(":")[2].split("\\.")[1]));
			}			
		}
		/**
		 * Test method for {@link view.TextDataImage#raSexagesimal(Double ra)}.
		 */
		@Test//(timeout = DEFAULT_TIMEOUT)
		public final void testRaSexagesimal() {
			//FIXME minute overflow
			int testRuns=100;
			for (int i = 0; i < testRuns; i++) {
				Double ra=(Double) MyMath.randomValueByRange(Double.class, 0.0, 359.999999, '*');
//				ra=0.0;
				System.out.println(ToolBox.getCurrentMethodName() + ": " + ra);
				
				Double sign = Math.signum(ra);
				
				Double wrkVal=new Double(Math.abs(ra));
				Integer expectedHours = wrkVal.intValue() /15    *sign.intValue();
				
				wrkVal=wrkVal%1;
				Integer expectedMin = ((Double) (wrkVal*60)).intValue();
				
				wrkVal=wrkVal-(expectedMin).doubleValue()/60;			
				Integer expectedSecs =  ((Double)(wrkVal*3600)).intValue();
				
				wrkVal=wrkVal-((expectedSecs.doubleValue())/3600);
				Double expectedMas = Math.rint((wrkVal*3600000));
				
				//correction of seconds at expectedMas overflow 
				if (expectedMas>=1000) {
					expectedMin+=1;
					expectedSecs=0;
				}
			
////				correction of minutes at expectedSecs overflow 
//				if (expectedMas>=1000) {
//					expectedMin+=1;
//					expectedSecs=0;
//				}
				
				String expectedStr = expectedHours + ":" + expectedMin + ":"
										+expectedSecs+"."+expectedMas;
				System.out.println(ToolBox.getCurrentMethodName() 
						+ "expectedStr: "+ expectedStr);
				//MUT
				String returnStr = TextDataImage.raSexagesimal(ra);
				//the test
				System.out.println(ToolBox.getCurrentMethodName() + ": "
						+ returnStr);
				assertEquals("degrees:", expectedHours, 
						new Integer(returnStr.split(":")[0]));
				assertEquals("minutes:", expectedMin, new Integer(returnStr
						.split(":")[1]));
				assertEquals("seconds:", expectedSecs, new Integer(returnStr
						.split(":")[2].split("\\.")[0]));
				assertEquals("milliarcseconds:", expectedMas.intValue(), new Integer(returnStr
						.split(":")[2].split("\\.")[1]));
			}			
		}
}
