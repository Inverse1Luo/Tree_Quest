package de.unistuttgart.treequest.app;

import de.unistuttgart.treequest.app.controller.TreeQuestController;
import de.unistuttgart.treequest.app.util.mvc.app.AbstractEntrance;
import de.unistuttgart.treequest.app.view.MainView;

public class TreeQuest extends AbstractEntrance {
	public TreeQuestController mainController;
	
	@Override
	protected void initManagerControllers() {
		mainController = new TreeQuestController(this, new MainView());
	}

	@Override
	protected void start() {
		showManagerFrame(mainController);
	}
	
	public static void main(String[] args) {
		TreeQuest treeQuest = new TreeQuest();
		treeQuest.start();
	}
}
