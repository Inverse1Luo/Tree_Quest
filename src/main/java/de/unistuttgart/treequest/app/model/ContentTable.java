package de.unistuttgart.treequest.app.model;

import java.awt.BorderLayout;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import java.util.List;


import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;


import de.unistuttgart.vis.tweet.Tweet;

public class ContentTable extends JTable {


	private final class MicroBlogTableModel extends AbstractTableModel {

		private final String[] m_columnNames = {"Date","Place", "Text", "@", "#","url" };

		public MicroBlogTableModel() {

		}

		@Override
		public int getColumnCount() {

			return m_columnNames.length;
		}

		public String getColumnName(int column) {

			return m_columnNames[column];
		}

		@Override
		public int getRowCount() {

			if (baseEntries == null) {
				return 0;
			} else {
				return baseEntries.size();
			}
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (row >= orderedEntryList.size()) {
				return null;
			}
			return orderedEntryList.get(row);
		}

	}

	private class MyTableCellRenderer extends DefaultTableCellRenderer {

		private final JCheckBox booleanRenderer = new JCheckBox("");
		private final JLabel textRenderer = new JLabel("");

		public Component getTableCellRendererComponent(JTable table, Object Value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Object retValue;
			final Tweet t = (Tweet) Value;
			JComponent c = textRenderer;
			switch (column) {

			case 0:
				retValue = t.getDateStamp();
				break;
			case 1:
				retValue = t.getPlacename();
				break;
			case 2:
				retValue = t.getText();
				break;
			
			case 3:
				retValue = t.getAt_user();
				break;
			case 4:
				retValue = t.getHashtag();
				break;
			case 5:
				retValue = t.getUrl();
				break;
			default:
				retValue = new String("empty");
				break;
			}

			if (retValue instanceof Boolean) {
				booleanRenderer.setSelected((Boolean) retValue);
				c = booleanRenderer;
			} else {
				c = (JComponent) super.getTableCellRendererComponent(table, retValue, isSelected, hasFocus, row,
						column);
			}
			return c;
		}

	}


	// Variables

	private Collection<Tweet> baseEntries;

	private ContentView baseView;

	private final List<Tweet> orderedEntryList = new ArrayList<>();

	private final MicroBlogTableModel tableModel;

	// Constructor
	public ContentTable(ContentView baseView) {
		this.baseView = baseView;
		this.setLayout(new BorderLayout());
		tableModel = new MicroBlogTableModel();
		this.setModel(tableModel);
		this.setDefaultRenderer(Object.class, new MyTableCellRenderer());

		this.setRowHeight(20);

		this.setFillsViewportHeight(true);
	}

	public void setDocuments(Collection<Tweet> documents) {
		this.setVisible(false);
		this.baseEntries = documents;
		orderedEntryList.clear();
		orderedEntryList.addAll(this.baseEntries);
		Collections.shuffle(orderedEntryList);
		tableModel.fireTableDataChanged();
		this.setVisible(true);

		repaint();
	}

}
