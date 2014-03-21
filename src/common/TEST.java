/**
 * Common Static Convinient Class for Tests
 */
package common;

import java.util.Date;
import java.util.Random;

public class TEST {
	public final static String LINEBREAK = System.getProperty("line.separator");

	public final static String SEPERATOR = System.getProperty("file.separator");

	public final static String TMPDIR = System.getProperty("java.io.tmpdir");

	public final static String USERDIR = System.getProperty("user.dir");

	public static Random random = new Random();

	public final static int DEFAULT_TIMEOUT = 60000; // one minute in milis

	public String[] allHSqlDBClassNames = { "INTEGER", "DOUBLE", "VARCHAR",
			"CHARACTER", "LONGVARCHAR", "DATE", "TIME", "TIMESTAMP", "DECIMAL",
			"NUMERIC", "BIT", "TINYINT", "SMALLINT", "BIGINT", "REAL",
			"BINARY", "VARBINARY", "LONGVARBINARY", "OTHER" };

	public static final String testDBsuffix = "TestDB";

	public static String dbURL = "jdbc:hsqldb:hsql://localhost/observations"
			+ testDBsuffix;

	public static String dbFile = "jdbc:hsqldb:file:lib/hsqldb/DB/observations"
			+ testDBsuffix;
	
	
	public static Class[] valueRangeClasses = { boolean.class, char.class,
			byte.class, short.class, int.class, long.class, float.class,
			double.class, String.class, Date.class };
}
