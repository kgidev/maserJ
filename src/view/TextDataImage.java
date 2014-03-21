/**
 * 
 */
package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.HashedMap.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import controller.MaserJ;
import controller.ObservationImageBuilder;

import tools.MyImage;
import tools.MyMath;
import tools.ToolBox;

/**
 * Draw the TextData to this Image
 * 
 */
public class TextDataImage extends BufferedImage {
	// TODO ,textwrap, sort - order of fields, numberFormats
	private static int imageType = MaserJ.BIMG_TYPE;

	protected HashMap<String, Object> textData;

	protected static int defaultHeight = 250;

	protected static int defaultWidth = 140;

	protected int height = super.getHeight();

	protected int width = super.getWidth();

	protected int padding = 0;

	protected Graphics2D g2d = this.createGraphics();

	protected FontMetrics fontMetrics = g2d.getFontMetrics();

	protected Color fgColor = Color.BLACK;

	protected Color bgColor = Color.WHITE;

	protected Color inverseColor = new Color(183, 215, 250); // light-blue

	protected String headerStr = "";

	protected String keySuffix = " :";

	protected String valueIdent = "   ";

	protected int lineHeight = fontMetrics.getMaxAscent() + 6;

	protected boolean hideID = true;

	String[] textOrder;

	/**
	 * @param width
	 * @param height
	 * @param textData
	 * @param headerStr
	 * @param textOrder
	 */
	public TextDataImage(int width, int height,
			HashMap<String, Object> textData, String headerStr,
			String[] textOrder) {
		super(width, height, imageType);
		g2d = this.createGraphics();
		this.textData = textData;
		this.headerStr = headerStr;
		this.textOrder = textOrder;
		MyImage.setBackgroundColor(this, bgColor);
		g2d.setColor(fgColor);
		g2d.drawRect(0, 0, width - 1, height - 1);
		// TODO with(textData),height(textData)
		drawTextData(textData);
	}

	/**
	 * 
	 * Default size
	 * 
	 * @param textData
	 * @param headerStr
	 */
	public TextDataImage(HashMap<String, Object> textData, String headerStr) {
		this(defaultWidth, defaultHeight, textData, headerStr, null);

	}

	/**
	 * 
	 * Default size
	 * 
	 * @param textData
	 * @param headerStr
	 */
	public TextDataImage(HashMap<String, Object> textData, String headerStr,
			String[] textOrder) {
		this(defaultWidth, defaultHeight, textData, headerStr, textOrder);
	}

	/**
	 * 
	 * @param width
	 * @param height
	 * @param textData
	 * @param headerStr
	 */
	public TextDataImage(int width, int height,
			HashMap<String, Object> textData, String headerStr) {
		this(width, height, textData, headerStr, null);
	}

	/**
	 * â
	 * 
	 * @param textData
	 *            the text to Draw on Image
	 */
	public void drawTextData(HashMap<String, Object> textData) {
		// TODO sort keys upper & lowerCase from DB !
		CaseInsensitiveMap textDataCaseInsensitive = new CaseInsensitiveMap(textData);
		textData=new HashMap<String, Object>(textDataCaseInsensitive);
		String[] keys = textData.keySet().toArray(new String[1]);
		keys = ToolBox.toLowerCase(keys);
		
		
		if (textOrder != null)
			keys = sortKeys(keys, textOrder);
		// Object[] values=textData.values().toArray(new Object[1]);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(fgColor);
		Font font = g2d.getFont();
		Font fontBold = font.deriveFont(Font.BOLD);
		g2d.setFont(fontBold);
		String headerText = headerStr.toUpperCase();
		int textLeft = 6;// this.getWidth()/2-fontMetrics.stringWidth(headerText)/2;
		g2d.drawString(headerText, textLeft, lineHeight);
		g2d.setColor(fgColor);
		g2d.fillRect(1, lineHeight + 4, this.getWidth() - 2, 2);
		g2d.setFont(font);
		g2d.setColor(inverseColor);
		int y = 2 * lineHeight + 10;
		for (int i = 0; i < keys.length; i++) {
			if (StringUtils.containsIgnoreCase(keys[i], "ID") && hideID)
				continue;// no DB-IDS
			g2d.setColor(fgColor);
			g2d.drawString(keys[i].toUpperCase() + keySuffix, textLeft, y);
			y = y + lineHeight;
			g2d.setColor(fgColor);
			String value = textData.get(keys[i]).toString();
			// numbers
			if (MyMath.isNumericClass(ClassUtils.primitiveToWrapper(textData
					.get(keys[i]).getClass()))) {
				value = ObservationImage.decimalFormat.format(textData
						.get(keys[i]));
			}
			// Right Ascension
			if (keys[i].equals("ra")) {
				value = raSexagesimal((Double)textData.get(keys[i]));
			}
			//declination
			if (keys[i].equals("dec")) {
				value = decSexagesimal((Double)textData.get(keys[i]));
			}
//			observation-Date
			if (keys[i].equals("date")) {
				value = DateFormatUtils.format((Date)textData.get(keys[i]), "yyyy-MM-dd");
			}
			
			// word-wrap ?
			String[] lines = lines(value);
			for (int j = 0; j < lines.length; j++) {
				g2d.setColor(inverseColor);
				g2d.fillRect(1, y - lineHeight + 4, this.getWidth() - 2,
						lineHeight);
				g2d.setColor(fgColor);
				g2d.drawString(lines[j] + " ", fontMetrics
						.stringWidth(valueIdent), y);
				y = y + lineHeight;
			}
		}
		g2d.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
	}

	/**
	 * calculate image width from TextData
	 */
	int setWidth(HashMap<String, Object> textData) {
		String maxLenghtString = getMaxLengthStringFromMap(textData);
		int minWidth = fontMetrics.stringWidth(maxLenghtString);
		int w = minWidth + padding;
		this.width = w;
		return w;
	}

	/**
	 * calculate image height from TextData
	 */
	int setHeight(HashMap<String, Object> textData) {
		// mapentrys+Header+lineSpace+padding
		int h = lineHeight * (textData.size() + 1) * 2 + padding;
		this.height = h;
		return h;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * 
	 * @param map
	 *            to be scanned
	 * @return longest string in Map
	 */
	public String getMaxLengthStringFromMap(HashMap<String, Object> map) {
		String maxLenghtString = "";
		MapIterator it = (new HashedMap(textData)).mapIterator();
		while (it.hasNext()) {
			Object key = it.next();
			if (key.toString().length() > maxLenghtString.length())
				maxLenghtString = key.toString() + keySuffix;
			Object value = it.getValue();
			if (value.toString().length() > maxLenghtString.length())
				maxLenghtString = value.toString() + valueIdent;
		}
		return maxLenghtString;
	}

	// /**
	// * @return new TextDataImage with minimun size to show all Data
	// */
	// public TextDataImage size2fit() {
	// int w=140;//(setWidth(textData)>minWidth)?setWidth(textData):minWidth;
	// int h=250;//(setHeight(textData)>minWidth)?setHeight(textData):minHeight;
	//		
	// return new TextDataImage(w,h,textData,headerStr);
	// }

	/**
	 * @param keyArray
	 *            to sort
	 * @param sortOrder
	 * @return sorted keyArray
	 */
	public String[] sortKeys(String[] keys, String[] keyOrder) {
		String[] sortKeys = keys.clone();
		sortKeys = ToolBox.toLowerCase(sortKeys);
		keyOrder = ToolBox.toLowerCase(keyOrder);

		for (int i = 0; i < keys.length; i++) {
			try { // only if keyOrder has keyOrder[i] element
				if (ArrayUtils.contains(sortKeys, keyOrder[i])) {
					sortKeys = (String[]) ArrayUtils.removeElement(sortKeys,
							keyOrder[i]);
					sortKeys = (String[]) ArrayUtils.add(sortKeys, i,
							keyOrder[i]);
				}
			} catch (RuntimeException e) {
				// e.printStackTrace();
			}
		}
		return sortKeys;
	}

	/**
	 * @return the amount of lines to this Image-Width & Font
	 */
	public String[] lines(String str) {
		int stringWidth = fontMetrics.stringWidth(str + valueIdent);
		int lineLength = getWidth();
		Double lineRatio = (double) stringWidth / (double) lineLength;
		int charPerLine = ((Double) Math.floor(str.length() / lineRatio))
				.intValue() - 1;

		String[] lines = new String[((Double) Math.ceil(lineRatio)).intValue()];
		for (int i = 0; i < lines.length; i++) {
			int endIndex = ((i + 1) * charPerLine > str.length()) ? str
					.length() : (i + 1) * charPerLine;
			lines[i] = str.substring(i * charPerLine, endIndex);
		}
		return lines;
	}

	/** Format a sexagesimal coordinate string.
     * @param value     A double precision value which is to be
     *                  converted to a string representation.  
     *                  The user should convert
     *                  to hours prior to this call if needed.
     * @param precision A integer value giving the precision to which
     *                    the value is to be shown.
     *  
     * <dl> 
     *   <dt> &le= 0 <dd> Degrees (or hours), e.g. 24
     *   <dt> 1 <dd> Deg.f <it> e.g.</it>,  24.3
     *   <dt> 2 <dd> Deg mm <it> e.g.</it>, 24 18
     *	  <dt> 3 <dd> Deg mm.f <it> e.g.</it>, 25 18.3
     *	  <dt> 4 <dd> Deg mm ss <it> e.g.</it>,  25 18 18
     *	  <dt> 5 <dd> Deg mm ss.s <it> e.g.</it>, 25 18 18.2
     *	  <dt> 6 <dd> Deg mm ss.ss <it> e.g.</it>, 25 18 18.24
     *	  <dt> &gt;6 <dd> Deg mm ss.sss <it> e.g.</it>, 25 18 18.238
     * </dl>
     */
   public static String sexagesimal(double value, int precision) {
   
   
       int    deg, min, sec, frac;
	double fdeg, fmin, fsec, ffrac;
	long   nelem;
	
	double[] offset = {
	   0.5, 0.05, 0.5/60, 0.05/60, 0.5/3600
	};
	
	double delta;
	
	if (precision > 9) {
	    precision = 9;
	}
	
	if (precision < 0) {
	    delta = offset[0];
	    
	} else if (precision >= offset.length) {
	    delta = offset[offset.length-1] / 
	             Math.pow(10,precision-offset.length+1);
	} else {
	    delta = offset[precision];
	}

	StringBuffer str = new StringBuffer();
	
	
	if (value < 0) {
	    value = Math.abs(value);
	    str.append("-");
	}
	
	
	DecimalFormat f = new DecimalFormat("#00");
	
	if (precision <= 0) {
	    str.append(f.format(new Double(value)));
	  
	} else if (precision == 1) {
	    f = new DecimalFormat("#00.0");
	    str.append(f.format(new Double(value)));
	    
	} else {
	    
	    value += delta;
	    if (precision == 2) {
	       nelem = (long)(60*value);
	    
	       deg = (int) (nelem/60);
	       min = (int) (nelem%60);
	       str.append(f.format(new Integer(deg))+" "+
			  f.format(new Integer(min)));
	    
	    } else if (precision == 3) {
	
	        nelem = (long) (600*value);
	  
	        deg  = (int) (nelem/600);
	        min  = (int) (nelem%600)/10;
	        frac = (int) (nelem%10);
	        str.append(f.format(new Integer(deg)) + " " + 
			   f.format(new Integer(min)) + "." + frac);
	    
	    } else if (precision == 4) {
	
	        nelem = (long) (3600*value);
	  
	        deg = (int) (nelem/3600);
	        min = (int) (nelem%3600)/60;
	        sec = (int) (nelem%60);
	        str.append(f.format(new Integer(deg)) + " " + 
			   f.format(new Integer(min)) + " " + 
			   f.format(new Integer(sec)));
	    
	    } else {
	
	        double mult = Math.pow(10, precision-4);
		
	        long fracMod = (long) (mult);
		
	        nelem = (long)(3600*fracMod*value);
	    
	        deg = (int)(nelem / (3600*fracMod) );
	        min = (int)(nelem % (3600*fracMod) / (60*fracMod) );
		sec = (int)(nelem % (60*fracMod) / fracMod);
			    
	        str.append(f.format(new Integer(deg)) + " " + 
			   f.format(new Integer(min)) + " " + 
			   f.format(new Integer(sec)) + ".");
	    
		frac = (int) (nelem%fracMod);
	        int divisor = 1;
	        for (int i=5; i<precision; i += 1) {
		    divisor *= 10;
		}
	        while (divisor > 0) {
		    str.append((char)('0'+(frac/divisor)%10));
		    divisor /= 10;
		}
	    }
	}

	    
       return str.toString();
   }
	
   
   /**
	 * @return ra-Deg as String to sexagesimal Format mm ss.sss  e.g., 25 18 18.238
	 */
	public static String raSexagesimal(Double ra) {
		Double wrkval =ra;
		Double hours=ra/15;
		Double min = hours%1*60;
		Double sec=min%1*60;;
		Double remainder=(1000*(sec%1));
		String raSexagesimal=(hours.intValue())+":"+min.intValue() 
								+":"+sec.intValue()+"."+remainder.intValue();
		return raSexagesimal;
	}
   
	 /**
	 * @return ra-Deg as String to sexagesimal Format mm ss.sss  e.g., -89 18 18.238
	 */
	public static String decSexagesimal(Double dec) {
		
		String sexagesimal = (sexagesimal(dec, 7).replace(' ', ':'));
		String decSexagesimal=dec.intValue()+sexagesimal.substring(sexagesimal.indexOf(':'));
		return decSexagesimal;
	}
	
}
