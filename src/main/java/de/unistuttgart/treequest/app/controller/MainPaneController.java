package de.unistuttgart.treequest.app.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.unistuttgart.treequest.app.model.InfmaTree;
import de.unistuttgart.treequest.app.model.InfmaTreeLens;
import de.unistuttgart.treequest.app.model.InfmaTreeNode;
import de.unistuttgart.treequest.app.model.InfmaTreePoint;
import de.unistuttgart.treequest.app.model.LibLinearClassifier;
import de.unistuttgart.treequest.app.model.QueryManager;
import de.unistuttgart.treequest.app.util.mvc.controller.AbstractManagerController;
import de.unistuttgart.treequest.app.util.mvc.controller.AbstractServiceController;
import de.unistuttgart.treequest.app.view.MainPane;

public class MainPaneController extends AbstractServiceController {

	private MainPane mainPane;
	private InfmaTree tweetTree;
	private QueryManager queryManager;
	private LibLinearClassifier classifier;
	private InfmaTreeNode relNode;
	private InfmaTreeNode irrelNode;

	@Override
	protected void setPanel(JPanel jPanel) {
		this.mainPane = (MainPane) jPanel;
	}

	@Override
	protected void addActionEvents() {
		// add mouse events
		addMouseEvent(mainPane.scatterPane, "scatter");

		// add action events
		addActionEvent(mainPane.searchBtn, "Searchbtn");
		addActionEvent(mainPane.classifyBtn, "classifybtn");
		addActionEvent(mainPane.lensBtn, "lensbtn");
		addActionEvent(mainPane.relBtn, "relbtn");
		addActionEvent(mainPane.irrelBtn, "irrelbtn");
		addActionEvent(mainPane.irNBtn, "irnbtn");
		addActionEvent(mainPane.relNBtn, "relnbtn");
		addActionEvent(mainPane.zoomInBtn, "zoominbtn");
		addActionEvent(mainPane.zoomOutBtn, "zoomoutbtn");
		addActionEvent(mainPane.exploreBtn, "explorebtn");
		addActionEvent(mainPane.tsneBtn, "tsnebtn");
		addActionEvent(mainPane.timeRangeBtn, "timerangebtn");
		addActionEvent(mainPane.tagCloudBtn, "tagCloudBtn");
		addActionEvent(mainPane.saveModelBtn, "savemodelBtn");
		
	}

	@Override
	protected void showView(boolean isToShow) {
		mainPane.setVisible(true);
	}

	// Constructor
	public MainPaneController(AbstractManagerController managerController, JPanel jPanel) {
		super(managerController, jPanel);
	}

	// Setters and Getters
	public QueryManager getQueryManager() {
		return queryManager;
	}

	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public LibLinearClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(LibLinearClassifier classifier) {
		this.classifier = classifier;
	}

	public InfmaTree getTweetTree() {
		return tweetTree;
	}

	public void setTweetTree(InfmaTree theTree) {
		this.tweetTree = theTree;
	}

	// Mouse Methods
	public void mouseScatter(String command, MouseEvent e) {
		switch (command) {
		case "clicked":
			if (e.getButton() == MouseEvent.BUTTON1 && mainPane.scatterPane != null) {
				mainPane.scatterPane.getInfmaTree().selectNode(e.getX(), e.getY());
				InfmaTreeNode node = mainPane.scatterPane.getInfmaTree().getSelectedNode();
				mainPane.report.report(node);
				mainPane.tweetTable.update(node);
				mainPane.detail.report(node);
//				mainPane.scatterPane.setToolTipText(node.getHistory());
				renewMainPanel();

			}
			if (e.getButton() == MouseEvent.BUTTON1 && mainPane.scatterPane.getLens() != null) {
				if (mainPane.scatterPane.getLens().getSelectedPoints() != null) {
					mainPane.tweetTable.update(mainPane.scatterPane.getLens().getSelectedPoints());
					renewMainPanel();
				}

			}
			if (e.getButton() == MouseEvent.BUTTON1 && mainPane.scatterPane.getLens() == null) {
				System.out.println(e.getX()+" "+ e.getY());
				mainPane.scatterPane.getInfmaTree().selectPoint(e.getX(), e.getY());

				if (mainPane.scatterPane.getInfmaTree().getSelectedPoint() != null) {
					mainPane.tweetTable.update(mainPane.scatterPane.getInfmaTree().getSelectedPoint());
					renewMainPanel();
				}
			} else {
				break;
			}
			break;
		case "pressed":
//			mainPane.scatterPane
//					.setToolTipText(mainPane.scatterPane.getInfmaTree().getSelectedNode().getHistory());
			break;
		case "released":
//			System.err.println("entered");
			break;
		case "entered":
//			System.err.println("entered");
			break;
		case "moved":
			if (mainPane.scatterPane.getLens() != null) {
				InfmaTreeLens lens = mainPane.scatterPane.getLens();
				lens.moveLens(e.getX(), e.getY());
				lens.renewTags(tweetTree.findLensSelPoints(lens.getBoundingRect()));
				renewMainPanel();
			}
			break;
		case "exited":
//			System.err.println("exited");
			break;
		default:
			break;
		}
	}

	public void wheelScatter(MouseWheelEvent e) {
		if(mainPane.scatterPane.getLens() != null) {
			InfmaTreeLens lens = mainPane.scatterPane.getLens();
			lens.changeWidth(e.getWheelRotation());// 滚轮向前为-1， 向后为1;
			lens.renewTags(tweetTree.findLensSelPoints(lens.getBoundingRect()));
			renewMainPanel();
		}
		
	}

	// Action methods
	public void actionSearchbtn(String command) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((TreeQuestController) managerController).startSearchFrame();
			}
		});
	}

	public void actionClassifybtn(String command) {
		if (relNode != null && irrelNode != null) {
			classifier.train(relNode, irrelNode, queryManager.getFeatureNumber());
			classifier.predict(relNode.getPoints());
			classifier.predict(irrelNode.getPoints());
//			relNode.setClassified(true);
//			irrelNode.setClassified(true);
			relNode.calcDecValRange();
			irrelNode.calcDecValRange();

			
			renewMainPanel();
		}
	}

	public void actionLensbtn(String command) {
		if (mainPane.scatterPane.getLens() == null) {
			mainPane.scatterPane.setLens(new InfmaTreeLens(mainPane.scatterPane));
		} else {
			mainPane.scatterPane.setLens(null);
			renewMainPanel();
		}
	}

	public void actionRelbtn(String command) {
		InfmaTreePoint point = mainPane.scatterPane.getInfmaTree().getSelectedPoint();
		point.setRelB(1D);
		renewMainPanel();
	}

	public void actionIrrelbtn(String command) {
		InfmaTreePoint point = mainPane.scatterPane.getInfmaTree().getSelectedPoint();
		point.setRelB(-1D);
		renewMainPanel();
	}

	public void actionRelnbtn(String command) {
		if (mainPane.scatterPane.getInfmaTree().getSelectedNode() != null) {
			relNode = mainPane.scatterPane.getInfmaTree().getSelectedNode();
			tweetTree.setRelNode(relNode);
			for(InfmaTreePoint point: relNode.getPoints()) {
				point.setRelQ(1D);
			}
		}
	}

	public void actionIrnbtn(String command) {
		if (mainPane.scatterPane.getInfmaTree().getSelectedNode() != null) {
			irrelNode = mainPane.scatterPane.getInfmaTree().getSelectedNode();
			tweetTree.setIrrelNode(irrelNode);
			for(InfmaTreePoint point: irrelNode.getPoints()) {
				point.setRelQ(-1D);
			}
		}
	}

	public void actionZoominbtn(String command) {
		mainPane.scatterPane.zoomIn();
		renewMainPanel();
	}

	public void actionZoomoutbtn(String command) {
		mainPane.scatterPane.zoomOut();
		renewMainPanel();
	}

	public void actionExplorebtn(String command) {
		mainPane.scatterPane.getInfmaTree().explore();
		renewMainPanel();
	}

	public void actionTsnebtn(String command) {
		InfmaTreeNode node = mainPane.scatterPane.getInfmaTree().getSelectedNode();
		if (node != null && node.getTweets().size() > 0) {
			node.useTSNE = !node.useTSNE;
		}
		mainPane.scatterPane.repaint();
	}

	public void actionTagcloudbtn(String command) {
		mainPane.scatterPane.setShowTagCloud(!mainPane.scatterPane.isShowTagCloud());
		mainPane.scatterPane.repaint();
	}

	public void actionTimerangebtn(String command) {
		InfmaTreeNode node = mainPane.scatterPane.getInfmaTree().getSelectedNode();
		node.calcDateRange();
		node.setUseTimeRange(!node.isUseTimeRange());
		mainPane.scatterPane.repaint();

	}
	public void actionSavemodelbtn(String command) {
		classifier.saveModel();
	}

	public void setProgressBarValue(double value) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (mainPane.progressBar != null) {
					mainPane.progressBar.setValue((int) Math.round(value * 100));
				}
			}
		});
	}

	public void setProgressBarString(String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (mainPane.progressBar != null) {
					mainPane.progressBar.setString(text);
				}
			}
		});
	}

	public void renewMainPanel() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainPane.repaint();
//				mainPane.scatterPane.repaint();
			}
		});
	}

	public void displayNewTree(InfmaTree tweetTree) {
		try {
			mainPane.scatterPane.setInfmaTree(tweetTree);
			mainPane.minimap.setTheTree(tweetTree);
			renewMainPanel();
			setProgressBarValue(1);
			setProgressBarString("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
