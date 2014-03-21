package controller;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import model.CelestialObject;
import model.DataBase;
import model.MaserComponent;
import model.Observation;
import nom.tam.fits.FitsException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.time.DateFormatUtils;

import tools.MyImage;
import tools.ToolBox;
import view.ObservationImage;
import view.swing.PositionEditor;

public class MaserJ {
	public static DataBase DB=null;
	
	public static final String[] appModes = { "TEST", "DEV", "PROD" };

	public static final String APP_MODE = appModes[1];

	public final static String APP_HOME = System.getProperty("user.dir");

	public static final String DATE_FORMAT = "yyyyMMdd";

	public static final int BIMG_TYPE = BufferedImage.TYPE_INT_ARGB;

	public static final Class[] timeClasses = { java.util.Date.class,
			java.sql.Timestamp.class, java.sql.Time.class, java.sql.Date.class };

	// ground (not composed) classes used by MaserJ Application
	public static final Class[] groundClasses = { boolean.class, char.class,
			byte.class, short.class, int.class, long.class, float.class,
			double.class, String.class };

	// sql classes used by MaserJ Application
	public static final Class[] sqlClasses = { java.sql.Date.class,
			java.sql.Timestamp.class, java.sql.Time.class };
	Date today=new Date();
	File inputDir;
	File outputDir=new File("Output"+DateFormatUtils.format(today, "yyyyMMdd_HHmmss"));
	File[] inputFiles;
	CelestialObject celObj;

	/**
	 * standatt Constructor mainly for tests
	 */
	MaserJ() {
		
	}
	
	/**
	 * @param arg0
	 *            pathName of Input-Directory
	 * @throws Exception
	 */
	MaserJ(String arg0) throws Exception {
		//startDB 
		DB=new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
		
		//create Outpur Directory
		outputDir=new File("Output"+DateFormatUtils.format(today, "yyyyMMdd_HHmm"));
		FileUtils.forceMkdir(outputDir);
		
		// readInput
		setInputDir(parseInputDir(arg0));
		setInputFiles(inputFiles(getInputDir()));
		if (inputFiles.length==0) {
			throw new IllegalArgumentException("No Input-Files in: "+arg0);
		}
		
		//create Model-Objects from input-Files: 
		//		1 celObj with n Obs, Obs with m Comps
		celObj = new CelestialObject(inputFiles[0]);
//		??if more then one celObj in Fits-Files, which one is the right one?
		
//		 TODO ObservationSeries obsSeries = new ObservationSeries(celObject,dates);
		
		Observation[] observations=new Observation[inputFiles.length];
		for (int i = 0; i < observations.length; i++) {
			observations[i]=new Observation(inputFiles[i],celObj);
		} 
		
//		 TODO
		// Position-Editing x,y per comp, Jpanel ??
		//get Obs with no Pos-Values in Components
		Observation[] noPosObs = observations;
		editComponentPositions(noPosObs);
		
		// Input-Files loop
		for (int i = 0; i < inputFiles.length; i++) {
			File currentInputFile = inputFiles[i];
//			observations[i] = new Observation(currentInputFile,celObj);
			
//			System.out.println(ToolBox.getCurrentMethodName()
//					+ "PRE new Observation(inputFiles[i],celObj)"
//					+	inputFiles[i].getAbsolutePath());
					
		//TODO write new Observations(fits) @timestamp 02.03.2008 23:59:51 

			// Pattern recognition a) rough, b) fine => comps per Obs
			//create components
			MaserComponent[] components=Observation.componentsFromFitsFile(
					currentInputFile, observations[i]);
			observations[i].setComponents(components);
//			 Image Rendering
//			 TODO ObservationImageBuilder();//??output savePath
			ObservationImageBuilder obsImgBuilder = new ObservationImageBuilder(observations[i]);
			ObservationImage obsImg = obsImgBuilder.getObsImg();
			String imagePath = FilenameUtils.separatorsToSystem(
					outputDir.getAbsolutePath()
					+ "/Observation_"+i+".png");
			obsImgBuilder.save(obsImg, imagePath);
		}
		
		// Interpolation missing Dates in Series
		// TODO interPolateObservation(series,date);
		celObj.setObseravtions(observations);

//		 Movie production
		MyImage.pngs2mov(outputDir.getAbsolutePath());
	}

	/**
	 * Start Application
	 * 
	 * @param args
	 *            Path to Datafiles
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MaserJ application = new MaserJ(args[0]);
	}

	/**
	 * runs the application Logic
	 * 
	 * @throws Exception
	 */
	private void runApp() throws Exception {

	}

	/**
	 * validates and sets inputPath
	 * 
	 * @param args
	 *            Path to Datafiles
	 */
	public File parseInputDir(String path) {
		File dir = new File(path);
		if (!dir.isDirectory())
			throw new IllegalArgumentException("No such Directory: " + path);
		return dir;
	}

	/**
	 * //read the fits files from InputDir
	 * 
	 * @param args
	 *            Path to Datafiles
	 * @return inputFiles sorted by path
	 */
	public File[] inputFiles(File startDir) {
		String[] extensions = { "fits" };
		Collection<File> inputFiles = FileUtils.listFiles(startDir, extensions,
				true);
		File[] inputFilesArray = inputFiles.toArray(new File[0]);
		Arrays.sort(inputFilesArray);
		return inputFilesArray;
	}

	/**
	 * @return the startPath
	 */
	public File getInputDir() {
		return inputDir;
	}

	/**
	 * @return the inputFiles
	 */
	public File[] getInputFiles() {
		return inputFiles;
	}

	/**
	 * @param inputFiles
	 *            the inputFiles to set
	 */
	public void setInputFiles(File[] inputFiles) {
		this.inputFiles = inputFiles;
	}

	/**
	 * @param inputDir
	 *            the inputDir to set
	 */
	public void setInputDir(File inputDir) {
		this.inputDir = inputDir;
	}

	/**
	 * show a UserDialog for the Observations wich have MaserComponents without 
	 * positions values (null?!) 
	 * 
	 * @param observations wich needs position-editing 
	 * @return the edited Observations          
	 */
	public Observation[] editComponentPositions(Observation[] obs) {
		Observation[] editComponentPositions=obs;
		//TODO code
		//launch positionEditor
		new PositionEditor(obs);
		return editComponentPositions;
	}
	
}
