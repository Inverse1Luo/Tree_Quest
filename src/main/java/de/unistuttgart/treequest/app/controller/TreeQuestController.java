package de.unistuttgart.treequest.app.controller;

import javax.swing.JFrame;

import de.unistuttgart.treequest.app.model.InfmaTree;
import de.unistuttgart.treequest.app.model.LibLinearClassifier;
import de.unistuttgart.treequest.app.model.QueryManager;
import de.unistuttgart.treequest.app.util.mvc.app.AbstractEntrance;
import de.unistuttgart.treequest.app.util.mvc.controller.AbstractManagerController;
import de.unistuttgart.treequest.app.view.MainView;

public class TreeQuestController extends AbstractManagerController {

	// Add all views, models and service controllers here
	private MainView mainView;
	
	private QueryManager queryManager;
	private InfmaTree theTree;
	private LibLinearClassifier classifier;
	
	public MainPaneController mainPaneController;
	public SearchPaneController searchPaneController;

	@Override
	public void start() {
		showView(true);
	}

	@Override
	protected void linkViewsWithController(JFrame mainView) {
		// Assign JFrame to this manager controller
		this.mainView = (MainView) mainView;
		// Assign service views each service controller and internal frame controller
		mainPaneController = new MainPaneController(this, this.mainView.mainPane);
		searchPaneController = new SearchPaneController(this, this.mainView.searchView.searchPane);
	}

	@Override
	protected void linkModelsWithController() {
		// Assign models to each service controller and each manager controller
		// LuceneQuery

		
		// QueryManager
		queryManager = new QueryManager(mainView.mainPane.progressBar);
		mainPaneController.setQueryManager(queryManager);
		searchPaneController.setQueryManager(queryManager);
		
		// InfmaTree
		theTree = new InfmaTree(null);
		mainPaneController.setTweetTree(theTree);
		searchPaneController.setInfmaTree(theTree);
		
		// LiblinearClassifier
		classifier = new LibLinearClassifier();
		mainPaneController.setClassifier(classifier);
	}

	@Override
	protected void addActionEvents() {
		// empty method, since no actions can be listened in this JFrame
	}

	@Override
	protected void showView(boolean isToShow) {
		// set JFrame to be visible or not
		mainView.setVisible(isToShow);
	}
	
	public TreeQuestController(AbstractEntrance entrance, JFrame mainFrame) {
		super(entrance, mainFrame);
	}
	
	public InfmaTree getTheTree() {
		return theTree;
	}
	
	public void startSearchFrame() {
		this.mainView.searchView.show();
	}

	public void displayNewTree(InfmaTree tweetTree) {
		this.mainPaneController.displayNewTree(tweetTree);
	}
	
}
