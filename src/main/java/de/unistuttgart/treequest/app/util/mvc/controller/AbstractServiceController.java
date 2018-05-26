package de.unistuttgart.treequest.app.util.mvc.controller;

import javax.swing.JPanel;


public abstract class AbstractServiceController extends AbstractController{
	protected AbstractManagerController managerController;
	
	protected abstract void setPanel(JPanel jPanel);
	
	public AbstractServiceController(AbstractManagerController managerController, JPanel jPanel) {
		setManagerController(managerController);
		setPanel(jPanel);
		addActionEvents();
	}
	
	public void setManagerController(AbstractManagerController managerController) {
		this.managerController = managerController;
	}
}
