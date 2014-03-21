package view.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import model.MaserComponent;

import org.apache.commons.lang.ArrayUtils;

import tools.ToolBox;

import com.fdsapi.arrays.ArrayComparator;

/**
 */
public class MaserComponentsTable extends JPanel implements TableModelListener {
	private boolean DEBUG = false;

	private boolean changeFlag = false;

	Color inverseColor = new Color(183, 215, 250); // TODO central

	int editMode = 9;// 9=all,0=nothing,1=application

	Color bgColor = Color.WHITE;

	Color fgColor = Color.BLACK;

	public static MaserComponent[] mComps;

	// one change ind Model = {MaserComponentObject, ColumName, value}
	Object[] change = new Object[3];

	ArrayList<Object[]> changeList = new ArrayList<Object[]>();

	private static JFrame frame;

	public MaserComponentsTable(MaserComponent[] mComps) {
		super(new GridLayout(1, 0));
		this.mComps = mComps;
		JTable table = new JTable(new MyTableModel(mComps));
		table.getModel().addTableModelListener(this);
		table.setPreferredScrollableViewportSize(new Dimension(900, 600));
		table.setGridColor(fgColor);
		table.setShowGrid(true);
		 table.setDefaultRenderer(Double.class, new ColorRenderer());
//		Set up column sizes
		initColumnSizes(table);

		// table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);

	}

	/*
	 * This method picks good column sizes. If all column heads are wider than
	 * the column's cells' contents, then you can just use
	 * column.sizeWidthToFit().
	 */
	private void initColumnSizes(JTable table) {
		MyTableModel model = (MyTableModel) table.getModel();
		int columnCount = table.getColumnModel().getColumnCount();
		TableCellRenderer headerRenderer = table.getTableHeader()
				.getDefaultRenderer();
		for (int i = 0; i < columnCount; i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setResizable(true);
			Component comp = headerRenderer.getTableCellRendererComponent(null,
					column.getHeaderValue(), false, false, 0, 0);
			int headerWidth = comp.getPreferredSize().width;
			comp = table.getDefaultRenderer(model.getColumnClass(i))
					.getTableCellRendererComponent(table,
							model.getValueAt(0, i), false, false, 0, i);
			int cellWidth = comp.getPreferredSize().width;
			column.setMinWidth(Math.max(headerWidth, cellWidth));
		}
	}

	class MyTableModel extends AbstractTableModel {

		private String[] columnNames;

		int rowCount;

		Collection row;

		private Object[][] data;

		String celObjName;

		// TODO input2data setData(input2data) constructor ?

		/**
		 * @param columnNames
		 * @param rowCount
		 * @param row
		 * @param data
		 */
		MyTableModel(MaserComponent[] mComps) {
			super();

			String celObjColHeader = "Celestial Object";
			String obsDateColHeader = "Observation Date";
			String celObjName = mComps[0].getObservation().getCelObject()
					.getName();
			Date obsDate = mComps[0].getObservation().getDate();
			String[] modelCols = mComps[0].getDataMap().keySet().toArray(
					new String[0]);
			this.columnNames = new String[] { celObjColHeader, obsDateColHeader };
			
			String[] customCols = ToolBox.sortKeys(modelCols, mComps[0].getKeyOrder());
			this.columnNames = (String[]) ArrayUtils.addAll(this.columnNames, customCols);

			Collection tmpCollection = new ArrayList();
			tmpCollection.add(celObjName);
			tmpCollection.add(obsDate);
			tmpCollection.addAll(mComps[0].getDataMap().values());
			this.row = tmpCollection;
			this.rowCount = this.row.size();
			// System.out.println("MyTableModel
			// row:"+Arrays.toString(this.row.toArray()) );
			this.data = getDataFromModelObject(mComps);
		}

		public Object[][] getDataFromModelObject(MaserComponent[] mComps) {
			// lastCol for model SOURCE-OBJECT
			Object[][] modelData = new Object[rowCount][row.size() + 1];
			for (int i = 0; i < modelData.length; i++) {
				
				// name of CelestialObject
				celObjName = mComps[i].getObservation().getCelObject()
						.getName();
				modelData[i][0] = celObjName;
				// ObservationDate
				modelData[i][1] = mComps[i].getObservation().getDate();
				// data of MaserComponent
				//TODO rearrange, skip ids ?
				
				Object[] compRowArr = 
					ToolBox.sortHashMapValuesByKeyOrder(mComps[i].getDataMap(),
							mComps[0].getKeyOrder());
				System.arraycopy(compRowArr, 0, modelData[i], 2,
						compRowArr.length);
				// MEMORIZE SOURCE-OBJECT in last cell, wich is not displayed
				modelData[i][row.size()] = mComps[i];
			}
			// TODO; set & sort
//			modelData = sortTableData(modelData);
			return modelData;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int valuePos) {
			return getValueAt(0, valuePos).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's editable.
		 */
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			// TODO change by editMode col = ...
			if (col < 2) {
				return false;
			} else {
				return true;
			}
		}

		/*
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at " + row + "," + col
						+ " to " + value + " (an instance of "
						+ value.getClass() + ")");
			}
			// setBackground ( Color.RED );
			data[row][col] = value;

			// write changes to changeList to commit it later
			change[0] = getValueAt(row, this.rowCount);
			change[1] = getColumnName(col);
			change[2] = value;
			changeList.add(change);
			System.out.println("change: " + Arrays.toString(change));
			System.out.println("changeList: " + Arrays.toString(change));

			fireTableCellUpdated(row, col); // calls tableChanged() in
			// sourounding Class

			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
			// // write changes to changeList to commit it later
			// change[0] = getValueAt(row,getColumnCount());
			// change[1] = getColumnName(col);
			// change[2] = value;
			// changeList.add(change);
			// System.out.println("change: "+Arrays.toString(change));
			// System.out.println("changeList: "+Arrays.toString(change));
		}

		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();

			for (int i = 0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j = 0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}

		/**
		 * sort the table 
		 */
		public Object[][] sortTableData(Object[][] tableData) {
			// handle nulls ??
			Object[][] sortedtableData = tableData.clone();

			ArrayComparator arrComp = new ArrayComparator();
			ArrayComparator ac = new ArrayComparator();
			int xOffsetColIndex = ArrayUtils.indexOf(columnNames, "Observation Date");
			ac.addSortCol(xOffsetColIndex, "asc"); 
			int yOffsetColIndex = ArrayUtils.indexOf(columnNames, "name");
			ac.addSortCol(yOffsetColIndex, "asc"); 
			ac.sort(sortedtableData);
			// TODO ??
			for (int i = 0; i < sortedtableData.length; i++) {
				System.out.println(sortedtableData[i]);
			}
			return sortedtableData;
		}

		
		
	}

	static class ColorRenderer extends DefaultTableCellRenderer {
		// call as CellRenderer
		public ColorRenderer() {
			super();
		}

		public void setValue(Object value) {
			// TODO set TextColor Grey

			Double s = new Double(value.toString());
			String cellValue = s.toString();
			// System.out.println("cellValue"+s);
			if (s.equals(Double.MIN_VALUE)) {
//				System.out.println("cellValue" + s);

				// setForeground();
				setBackground(Color.RED);
				cellValue = "NULL";
			} else {
				setForeground(Color.BLACK);
				setBackground(Color.WHITE);
			}

			setText(cellValue);
		}
	}

	public void tableChanged(TableModelEvent e) {
		setChangeFlag(true);
		System.out.println("setChangeFlag(true)");
		// TODO error java.lang.NullPointerException?!
		// frame.setTitle(frame.getTitle()+ " **DATACHANGED**");
		int row = e.getFirstRow();// nonsense
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		String columnName = model.getColumnName(column);
		// System.out.println("model.getValueAt(0, 0): " + model.getValueAt(0,
		// 0));
		Object data = model.getValueAt(row, column);
		// System.out.println("tableChanged,columnName: " + columnName);
		// System.out.println("tableChanged,getSource: ");
		if (DEBUG) {
			// wich MaserComponent to update
			// changedCompsData
			// setChangeFlag(true);
			System.out
					.println(ToolBox.getCurrentMethodName() + " Pre Commit: ");
			for (int i = 0; i < mComps.length; i++) {
				ToolBox.dumpHahsMap(mComps[i].getDataMap());
			}
		}
		commitChanges(changeList);
	}

	// commitChanges
	public void commitChanges(ArrayList<Object[]> changeList) {
		// TODO write the changed Data to the MaserComponentsInputObjects,
		// atomic ?!
		// System.out.println("commitChanges changeList: " + changeList);
		for (Object[] change : changeList) {
			// set value in ModelObject ()
			int j = ArrayUtils.indexOf(mComps, change[0]);
			mComps[j].getDataMap().put((String) change[1], change[2]);
			if (DEBUG) {
				System.out.println("commitChanges: " + Arrays.toString(change));
			}
			// TODO set key,Value in MaserCompObject.getDataMap();
		}

		if (DEBUG) {
			// System.out.println("tableChanged,mComp: "+mComp);
			// System.out.println("tableChanged,changedData: "+changedData);
			// wich MaserComponent to update
			// changedCompsData
			// setChangeFlag(true);
			System.out.println(ToolBox.getCurrentMethodName()
					+ " POST Commit: ");
			for (int i = 0; i < mComps.length; i++) {
				ToolBox.dumpHahsMap(mComps[i].getDataMap());
			}
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		frame = new JFrame("MaserComponentsTable");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		MaserComponentsTable newContentPane = new MaserComponentsTable(mComps);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * @return the changeFlag
	 */
	public boolean isChangeFlag() {
		return changeFlag;
	}

	/**
	 * @param changeFlag
	 *            the changeFlag to set
	 */
	public void setChangeFlag(boolean changeFlag) {
		this.changeFlag = changeFlag;
	}
}