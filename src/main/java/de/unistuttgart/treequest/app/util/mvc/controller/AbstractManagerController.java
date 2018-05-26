package de.unistuttgart.treequest.app.util.mvc.controller;

import javax.swing.JFrame;

import de.unistuttgart.treequest.app.util.mvc.app.AbstractEntrance;

public abstract class AbstractManagerController extends AbstractController {
	protected AbstractEntrance entrance;
	
	// set pre-defined methods for manager controller
	public abstract void start();

	protected abstract void linkViewsWithController(JFrame mainFrame);

	protected abstract void linkModelsWithController();
	
	public AbstractManagerController(AbstractEntrance entrance, JFrame mainFrame) {
		setEntrance(entrance);
		linkViewsWithController(mainFrame);
		linkModelsWithController();
		addActionEvents();
	}
	
	public void setEntrance(AbstractEntrance entrance) {
		this.entrance = entrance;
	}
}
