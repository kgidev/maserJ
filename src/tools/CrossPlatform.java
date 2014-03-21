/**
 * 
 */
package tools;

import org.apache.commons.lang.StringUtils;


/**
 * CrossPlatform helper Class and methods
 */
public class CrossPlatform {
	public final static String LINEBREAK = System.getProperty("line.separator");
	public final static String SEPERATOR = System.getProperty("file.separator");
	public final static String TMPDIR = System.getProperty("java.io.tmpdir");

	public static String path(String path) {
		//convert to Path on current platform
		//TODO UnitTest 
		String retPath=null;
		if (StringUtils.contains(path, '/')) retPath =  path.replaceAll("/", SEPERATOR);
		if (StringUtils.contains(path, '\\')) retPath =  path.replaceAll("\\", SEPERATOR);
		return retPath;
	}
	
}
