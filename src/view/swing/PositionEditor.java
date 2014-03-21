package view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import tools.ToolBox;

import model.CelestialObject;
import model.MaserComponent;
import model.Observation;

import controller.MaserJ;
import  com.fdsapi.arrays.ArrayComparator;

public class PositionEditor implements ActionListener, TableModelListener {
	final static boolean shouldFill = true;

	final static boolean shouldWeightX = true;

	final static boolean RIGHT_TO_LEFT = false;

	private static final boolean DEBUG = false;

	Observation[] observations;

	String editMode = "000"; // readonly,111 editAll, celObj,obs,comps...

	JFrame frame;

	Color inverseColor = new Color(183, 215, 250); // TODO central

	Color bgColor = Color.WHITE;
	
	Color fgColor = Color.BLACK;

	boolean editable = true;

	Border lineBorder = BorderFactory.createLineBorder(Color.black, 1);

	boolean changeFlag = false;

	Observation currentObs;

	// ref to celObjPanel
	ArrayList<JPanel> textPanels = new ArrayList<JPanel>(2);

	// to compare the initValues with currentValues
	HashMap<String, Object> initCelObjDataMap = new HashMap<String, Object>(0);

	HashMap<String, Object> initObsDataMap = new HashMap<String, Object>(0);

	ArrayList<HashMap<String, Object>> obsInitDataList = new ArrayList<HashMap<String, Object>>();

	HashMap<String, Object> compsInputValueMap = new HashMap<String, Object>(0);

	ArrayList<HashMap<String, Object>> compsInitDataList = new ArrayList<HashMap<String, Object>>();
	JTable tablePanel;
	MaserComponent[] allComps;
	ListSelectionModel listSelectionModel;
	MaserComponentsTable maserComponentsPanel;

	private Object[][] tableData;

	private String[] columnNames;

	private JPanel componentPanel;

	/**
	 * standart Constructor
	 */
	public PositionEditor(Observation[] observations) {
		this.observations = observations;		
		//set theComponents
		setAllComps(getAllCompsFromObs(observations));
//		 save the initValues from DataModel
		saveInitValues(observations);
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	class MyDocumentListener implements DocumentListener {
		final String newline = "\n";

		public void insertUpdate(DocumentEvent e) {
			updateLog(e, "inserted into");
			setChangeFlag(true);
		}

		public void removeUpdate(DocumentEvent e) {
			updateLog(e, "removed from");
			setChangeFlag(true);
		}

		public void changedUpdate(DocumentEvent e) {
			// Plain text components don't fire these events.
		}

		public void updateLog(DocumentEvent e, String action) {
			Document doc = (Document) e.getDocument();
			int changeLength = e.getLength();
			String appendStr = changeLength + " character"
					+ ((changeLength == 1) ? " " : "s ") + action + " "
					+ doc.getProperty("name") + "." + newline
					+ "  Text length = " + doc.getLength() + newline;
			// displayArea.append(appendStr);
			// displayArea.setCaretPosition(displayArea.getDocument().getLength());
			System.out.println("DocumentEvent: " + e.getDocument());
			System.out.println("DocumentEvent: " + e);
			System.out.println("DocumentListener: " + appendStr);

		}
	}
	
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            boolean isAdjusting = e.getValueIsAdjusting(); 
            System.out.println(
            "Event for indexes "
                          + firstIndex + " - " + lastIndex
                          + "; isAdjusting is " + isAdjusting
                          + "; selected indexes:");

            if (lsm.isSelectionEmpty()) {
            	System.out.println(" <none>");
            } else {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                    	 System.out.println(" " + i);
                    }
                }
            }
            System.out.println("lsm index: "+ lsm.getMinSelectionIndex());
        }
    }
	

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 * 
	 * @throws Exception
	 */
	private void createAndShowGUI() throws Exception {
		// Create and set up the window.
		frame = new JFrame("MaserJ  Position Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setChangeFlag(false);
		// Set up the content pane.
		addComponentsToPane(frame.getContentPane());

		// Display the window.
		int width = 800;
		int height = 600;
		frame.setSize(width, height);
		// frame.pack();
		frame.setVisible(true);
	}

	// Set up the content pane.
	public void addComponentsToPane(Container pane) {

		JButton button;
		GridBagLayout gridbag = new GridBagLayout();
		pane.setLayout(gridbag);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		
		componentPanel = componentPanel(allComps);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 5;
		gridbag.setConstraints(componentPanel, gridBagConstraints);
		pane.add(componentPanel, gridBagConstraints);
		
		gridBagConstraints.insets = new Insets(0,10,0,10);
		JButton okButton = new JButton("Set");
		okButton.addActionListener(this);
		// gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		// gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.25;
		gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_START;
		
		gridbag.setConstraints(okButton, gridBagConstraints);
		pane.add(okButton);

		JButton cancelButton = new JButton("Reset");
		cancelButton.addActionListener(this);
		// gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0.25;
		gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
		gridbag.setConstraints(cancelButton, gridBagConstraints);
		pane.add(cancelButton);
		
	}

	
	
	JPanel componentPanel(MaserComponent[] comps) {
		// TODO layout, editmodes,void setEditable(boolean)
		JPanel componentPanel = new JPanel();
		componentPanel.setLayout(new BorderLayout());
		componentPanel.setBackground(bgColor);
		JLabel headerLabel = new JLabel("MaserComponents");
		headerLabel.setBorder(lineBorder);
		componentPanel.add(headerLabel, BorderLayout.PAGE_START);
		componentPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		componentPanel.setBorder(BorderFactory.createEtchedBorder());
		componentPanel.setVisible(true);
		maserComponentsPanel = new MaserComponentsTable(comps);
//		maserComponentsPanel.ch
		//JScrollPane tablePanel = tablePanel(comps);
		componentPanel.add(maserComponentsPanel);
		return componentPanel;
	}



//	public JScrollPane tablePanel(MaserComponent[] comps) {
//		
//		
//		columnNames = comps[0].getDataMap().keySet().toArray(
//						new String[0]);
//		tableData = new Object[comps.length][];
//		for (int i = 0; i < comps.length; i++) {
//			HashMap<String, Object> dataMap = comps[i].getDataMap();
//			tableData[i] = dataMap.values().toArray();
//		}
//		tableData = sortTableDateByXYoffset(tableData);
//		tablePanel = new JTable(tableData, columnNames);
//		tablePanel.getModel().addTableModelListener(this);
//		tablePanel.setGridColor(fgColor);
//		tablePanel.setShowGrid(true);
//		listSelectionModel = tablePanel.getSelectionModel();
//	    listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
//	    listSelectionModel.setSelectionMode(
//                ListSelectionModel.SINGLE_SELECTION);
//	    tablePanel.setSelectionModel(listSelectionModel);
//		
////		tablePanel.setPreferredScrollableViewportSize(new Dimension(500, 70));
//		// tablePanel.getModel().addTableModelListener(
//		// new MyDocumentListener());
//
//		// Create the scroll pane and add the table to it.
//		JScrollPane scrollPane = new JScrollPane(tablePanel);
//		return scrollPane;
//
//	}

	/*
	 * general Event-Handling (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
//		 System.out.println(ToolBox.getCurrentMethodName() + "e.getSource(): " +  e.getSource());
		// System.out.println(ToolBox.getCurrentMethodName()
		// + "e.getActionCommand(): " + e.getActionCommand());
		// cancel -> init();

		if (e.getActionCommand().equals("Reset")) {
			try {
				componentPanel = componentPanel(allComps);
				//frame.repaint();
//				addComponentsToPane(frame.getContentPane());
//				createAndShowGUI();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		if (e.getActionCommand().equals("Set") && maserComponentsPanel.isChangeFlag()) {
			// System.out.println("msg answer:"
			// + ((JTextField) e.getSource()).getName());
			int userAnswer = JOptionPane.showConfirmDialog(frame,
					"save changes ?", e.getActionCommand(),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (userAnswer == 0) {
				// System.out.println("msg answer:"
				// + ((JTextField) e.getSource()).getName());
				commitChanges();
				// close window??
			}
		}
	}

	public static void main(String[] args) {
		PositionEditor posEditor = new PositionEditor(null);
	}

	/**
	 * save the initValues from Input to compare them later with changes
	 */
	public boolean saveInitValues(Observation[] observations) {
		boolean saveInitValues = false;
		// celOBJ
		initCelObjDataMap = (HashMap<String, Object>) observations[0]
				.getCelObject().getDataMap().clone();
		initCelObjDataMap.put("ObservationChilds", observations);
		// observationObjects
		for (int i = 0; i < observations.length; i++) {
			// observations
			initObsDataMap = (HashMap<String, Object>) observations[i]
					.getDataMap().clone();
			obsInitDataList.add(initObsDataMap);
		}
		// comps
		for (int i = 0; i < allComps.length; i++) {
			compsInputValueMap = (HashMap<String, Object>) allComps[i]
			    				.getDataMap().clone();
			compsInitDataList.add(compsInputValueMap);
		}
		return saveInitValues;
	}

	/**
	 * save changed Values
	 */
	public boolean commitChanges() {
		// TODO atomic with clones and commit or Undo?!
		boolean saveChanges = false;
		// celOBJ
		// loop over celObj.dataMap and save changes ??
		// panel.getTextFields
		//writeChanges to DB
		for (int i = 0; i < allComps.length; i++) {
//			updateRow()
			System.out.println("allComps[i].getID(): "+allComps[i].getID());
			allComps[i].updateDBRow();
		}
	
		
		
		
		for (int j = 0; j < textPanels.size(); j++) {
			// if (textPanels.get(j).getName().equals("CelestialObject")) {
			JPanel textPanel = textPanels.get(j);

			System.out.println("celObjTextPanel.getName(): "
					+ textPanel.getName());
			java.awt.Component[] panelComps = textPanel.getComponents();
			ToolBox.dumpHahsMap(observations[0].getCelObject().getDataMap());
			for (int i = 0; i < panelComps.length; i++) {
				if (JTextField.class.isInstance(panelComps[i])) {
					JTextField txtField = (JTextField) panelComps[i];
					System.out.println("panelComps[i].getName(): "
							+ txtField.getName());

					if (textPanel.getName().equals("Observation")) {
						System.out
								.println("Observation oldValue: "
										+ currentObs.getDataMap().put(
												txtField.getName(),
												txtField.getText()));
					}

					if (textPanel.getName().equals("CelestialObject")) {
						System.out.println("CelestialObject oldValue: "
								+ observations[0].getCelObject().getDataMap()
										.put(txtField.getName(),
												txtField.getText()));
					}

				}
//				ToolBox
//						.dumpHahsMap(observations[0].getCelObject()
//								.getDataMap());
			}
			//table-changes save the comps write all tableData to comps[]
			TableModel tableModel = tablePanel.getModel();
			//relies on same order of rows in tableModel  like in input (allComps) !
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				HashMap<String, Object> compDataMap = allComps[i].getDataMap();
				ToolBox.dumpHahsMap(compDataMap);
				for (int k = 0; k < tableModel.getColumnCount(); k++) {
					System.out.println("getColumnName: "+tableModel.getColumnName(k));
					System.out.println("getValueat: "+tableModel.getValueAt(i, k));
					compDataMap.put(tableModel.getColumnName(k), tableModel.getValueAt(i, k));
				}
				ToolBox.dumpHahsMap(allComps[i].getDataMap());
			} 
		}
		// }
		// observation
		// comps
		return saveChanges;
	}

	 public void tableChanged(TableModelEvent e) {
	        int row = e.getFirstRow();
	        int column = e.getColumn();
	        TableModel model = (TableModel)e.getSource();
	        String columnName = model.getColumnName(column);
	        Object data = model.getValueAt(row, column);
	        System.out.println("tableChanged,columnName: "+columnName);
	        System.out.println("tableChanged,getSource: ");
	        //wich MaserComponent to update
//	        changedCompsData
	        setChangeFlag(true);
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

	/**
	 * @return the allComps
	 */
	public MaserComponent[] getAllComps() {
		return allComps;
	}

	/**
	 * @param allObs the observations to get the Components from
	 */
	public MaserComponent[] getAllCompsFromObs(Observation[] allobs) {
		ArrayList<MaserComponent> allCompsFromObs=new ArrayList<MaserComponent>();
		for (int i = 0; i < allobs.length; i++) {
			MaserComponent[] compsFromObs = allobs[i].getComponents();
			if (compsFromObs!=null) {
				for (int j = 0; j < compsFromObs.length; j++) {
					allCompsFromObs.add(compsFromObs[j]);
				}
			}			
		}
		return  allCompsFromObs.toArray(new MaserComponent[allCompsFromObs.size()]);
	}

	/**
	 * @param allComps the allComps to set
	 */
	public void setAllComps(MaserComponent[] allComps) {
		this.allComps = allComps;
	}
	
	 


}