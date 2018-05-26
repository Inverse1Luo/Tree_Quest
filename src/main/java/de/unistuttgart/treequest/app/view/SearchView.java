package de.unistuttgart.treequest.app.view;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class SearchView extends JInternalFrame {

	public JPanel contentPane;
	public SearchPane searchPane;

	public static void main(String[] args) {
		SearchView searchView = new SearchView();
		searchView.setVisible(true);
	}

	public SearchView() {
		// Give title to search panel, make it as unresizable and closable
		super("Search Filters", false, true);
		this.contentPane = new JPanel();
		// Fill this frame with content panel
		this.contentPane.setLayout(new BorderLayout());
		this.setContentPane(contentPane);
		addPanels();
	}

	public void addPanels() {
		searchPane = new SearchPane();
		contentPane.add(searchPane, BorderLayout.CENTER);
	}

}
