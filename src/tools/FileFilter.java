/**
 * 
 */
package tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.lang.time.DateUtils;

import controller.MaserJ;

/**
 * 
 */
public class FileFilter implements FilenameFilter  {
	String filterString = "";
	boolean ret = false;
	final String SEPERATOR = System.getProperty("file.separator");
	boolean isValdiFilter=false;	

	public FileFilter(String filterName) {
		filterString = filterName;
	}

	public boolean accept(File dir, String name) throws IllegalArgumentException {
		boolean isLink = false;


		if (filterString.contains("directoryFilter")) {
			File f = new File(dir.getAbsolutePath() + SEPERATOR + name);
			try {
				isLink = !f.getCanonicalPath().equals(f.getAbsolutePath());
				// System.out.println(isLink);
				isValdiFilter=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (filterString.equals("directoryFilter")) {
				ret = (f.isDirectory() && !isLink);
				isValdiFilter=true;
			}
			if (filterString.equals("NodirectoryFilter")) {
				ret = !(f.isDirectory() && !isLink);
				isValdiFilter=true;
			}


		}
		
		if (filterString.equals("observation.xml")) {
			ret = name.equals("observation.xml");
			isValdiFilter=true;
		}
		
		if (filterString.equals("DateDirectory")) {
			File f = new File(dir.getAbsolutePath() + SEPERATOR + name);
			boolean accept=false;
			try {
				DateUtils.parseDate(f.getName(), new String[] {MaserJ.DATE_FORMAT});
				accept=true;
				
			} catch (ParseException e) {}
			ret = f.isDirectory() && accept;
			isValdiFilter=true;
		}
		
		if (filterString.equals("*Test.class")) {
			ret = name.endsWith("Test.class");
			isValdiFilter=true;
		}
		
		if (filterString.equals(".xml")) {
			ret = name.endsWith(".xml") || name.endsWith(".XML");
			isValdiFilter=true;
		}
		
		if (filterString.equals("")) {
			ret = true;
			isValdiFilter=true;
		}
		if (!isValdiFilter) throw new IllegalArgumentException("no such FileFilter : "
				+ dir);
		return ret; // no filtering
	}
}
