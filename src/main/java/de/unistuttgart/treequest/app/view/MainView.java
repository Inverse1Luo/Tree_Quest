package de.unistuttgart.treequest.app.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainView extends JFrame {

	public static final int WIDTH , HEIGHT;

	public JPanel contentPane;
	public JLayeredPane jLayeredPane;
	
	public MainPane mainPane;
	public SearchView searchView;
	
	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		WIDTH = screenSize.width;
		HEIGHT = screenSize.height;
	}

	public static void main(String[] args) {
		MainView mainView = new MainView();
		mainView.setLayout(new BorderLayout());
		mainView.setVisible(true);
	}

	public MainView() {
		// add window closing listener
		// pop up a confirm dialog when user is trying to exit
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int option= JOptionPane.showConfirmDialog( 
						MainView.this, "Exit system? ", "Confirm Exit", JOptionPane.YES_NO_OPTION); 
				if(option == JOptionPane.YES_OPTION){
					if(e.getWindow() == MainView.this){ 
						System.exit(0); 
					}
				} 
			}
		});
		
		// Initiate with the maximized window size
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		// Set a content pane with border layout.
		// This is the easiest way to make a JPanel fill the screen
		this.contentPane = new JPanel(new BorderLayout());
		this.setContentPane(contentPane);
		// Get the layered panel to put search view, which is a internal frame
		// Since an internal frame requires a container
		this.jLayeredPane = this.getLayeredPane();
		// Add concrete panels to contentPane and layeredPane.
		addPanels();
		// show the JFrame
		this.setVisible(true);
	}

	public void addPanels() {
		mainPane = new MainPane();
		contentPane.add(mainPane, BorderLayout.CENTER);
		
		searchView = new SearchView();
		searchView.setVisible(true);
		searchView.setSize((int)(0.4 * WIDTH), (int)(0.4 * HEIGHT));
		searchView.setLocation((int)(0.7 * WIDTH), (int)(0.2 * HEIGHT));
		searchView.pack();
		jLayeredPane.add(searchView);
	}

}
