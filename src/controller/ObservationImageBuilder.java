package controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;

import model.CelestialObject;
import model.CelestialObjectTest;
import model.DBTable;
import model.DBTableTest;
import model.Observation;
import tools.MyImage;
import view.ColorCodeImage;
import view.GridImage;
import view.MaserComponentsImage;
import view.ObservationImage;
import view.TextDataImage;

/**
 * Builder for one ObservationImage 
 * 1. calculates the inputs for the ObservationImage and its parts
 * 2. build the Image-parts  
 * 3. builds the ObservationImage with parts
 * 
 */
public class ObservationImageBuilder  {

//	//container image-propertys
	protected static final int IMG_TYPE = BufferedImage.TYPE_INT_ARGB;	
	protected static int height=600;
	protected static int width=800;

	protected Observation observation=null;
	
	protected ObservationImage obsImg;
	protected TextDataImage celestialObjectTextImg;
	protected TextDataImage observationTextImg;
	protected GridImage gridImg;
	protected ColorCodeImage colorCodeImg;
	protected MaserComponentsImage maserCompsImg;


	/**
	 * empty Constructor for testing
	 */
	protected ObservationImageBuilder()  {
	}
	
	/**
	 * @param width,height
	 * @param observation
	 * @throws Exception 
	 */
	ObservationImageBuilder(Observation observation) throws Exception  {
		setObservation(observation);
		setObsImg(build());
	}
	
	/**
	 * build the ObservationImage from all parts
	 * @return this ObservationImage
	 * @throws Exception 
	 */
	ObservationImage build()  {
//		 celestialObjectTextImg
		try {
//			 celestialObjectTextImg
			//parent of this observation
			CelestialObject celObj = observation.getCelObject();
			HashMap<String, Object> celestialObjectTextData = celObj.getDataMap();
			celestialObjectTextImg=new TextDataImage(celestialObjectTextData,"CELESTIALOBJECT",celObj.getKeyOrder()); 
//			 observationTextImg
			HashMap<String, Object> observationTextData = observation.getDataMap();
			observationTextImg=new TextDataImage(observationTextData,"OBSERVATION",observation.getKeyOrder()); 
//			ColorCodeImage
			//TODO velocity range from maserComponents for scale
			colorCodeImg=new ColorCodeImage(50,500);
//			GridImage
			//TODO x,y range from maserComponents for scale
			gridImg=new GridImage(600,600);			
//			TODO x,y range from maserComponents for scale ,components from Observation
			
			maserCompsImg=new MaserComponentsImage(500,500,observation.getComponents(),
					observation.isInterpolated());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		 gridImg;
//		 colorCodeImg;
//		 maserImg;
		return new ObservationImage(width,  height, gridImg, colorCodeImg,
				celestialObjectTextImg, observationTextImg, maserCompsImg);

	}
	
	/**
	 * @param bImg  a <code>BufferedImage</code> to be saved.
	 * @param imagePath  a <code>String</code> containg the path where the Image
	 * should be written.			
	 * @return success <code>true</code> if Image was written.
	 * @throws IOException  if an error occurs during writing.
	 */
	boolean save(BufferedImage bImg, String imagePath) throws IOException {
		boolean success =false;
		try {
			success=MyImage.saveImage(bImg, imagePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return success;
	}
	
	/**
	 * @return the observation
	 */
	public Observation getObservation() {
		return observation;
	}
	
	/**
	 * @param observation the observation to set
	 */
	public void setObservation(Observation observation) {
		this.observation = observation;
	}

	/**
	 * @return the obsImg
	 */
	public ObservationImage getObsImg() {
		return obsImg;
	}

	/**
	 * @param obsImg the obsImg to set
	 */
	public void setObsImg(ObservationImage obsImg) {
		this.obsImg = obsImg;
	}

	

}
