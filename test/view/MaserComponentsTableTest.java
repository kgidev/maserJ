/**
 * 
 */
package view;

import static org.junit.Assert.*;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;

import model.DBTable;
import model.DataBase;
import model.MaserComponent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.MaserJTest;
import controller.ObservationSeries;

import tools.ToolBox;
import view.swing.MaserComponentsTable;

public class MaserComponentsTableTest {

	static DataBase db;

	static Integer observationId;

	static MaserComponent maserComponent;

	static DBTable compTable;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		db = new DataBase(true, common.TEST.dbURL, common.TEST.dbFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		db.dropTables();
		db.closeDB();
		//db.shutdownDB();
		db = null;
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
	}

	/**
	 * Test method for {@link view.swing.MaserComponentsTable#MaserComponentsTable(model.MaserComponent[])}.
	 * @throws Exception 
	 */
	@Test
	public final void testMaserComponentsTable() throws Exception {
		
		//TODO automatic gui inputs 
		int numberOfObs=10;
		ObservationSeries obsSeries = MaserJTest.appTestData( numberOfObs);
		MaserComponent[] allMaserComps = obsSeries.getAllCompsFromObs();
		MaserComponentsTable maserComponentsPanel = new MaserComponentsTable(allMaserComps);
		
		JFrame frame = new JFrame("MaserComponentsTable");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create GIU and set up the content pane.
        Container newContentPane = frame.getContentPane();
        ((JComponent) newContentPane).setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.add(maserComponentsPanel);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
		
		//TODO change field-value and write it  
		ToolBox.sleep(1000*100); //show GUI longer Do it Better with thread...
		assertEquals("comps 0 xoffset ==9999", 9999.0, allMaserComps[0].getDataMap().get("xoffset"));
		fail("Not yet implemented"); // TODO
	}

}
