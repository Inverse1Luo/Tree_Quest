package de.unistuttgart.treequest.app.model;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import de.unistuttgart.treequest.app.model.threadsafe.ThreadsafeRectangle2D;


public class InfmaTreeLens {

	private int width = 20;

	private Rectangle2D boundingRect;

	private InfmaTreemap theView;

	private TagContainer tagContainer;

	private LinkedList<InfmaTreePoint> selectedPoints = new LinkedList<>();

	public InfmaTreeLens(InfmaTreemap view) {
		this.theView = view;
		this.boundingRect = new ThreadsafeRectangle2D(0, 0, 20, 20);
	}

	public LinkedList<InfmaTreePoint> getSelectedPoints() {
		return selectedPoints;
	}

	public void setSelectedPoints(LinkedList<InfmaTreePoint> selectedPoints) {
		this.selectedPoints = selectedPoints;
	}

	public TagContainer getTagContainer() {
		return tagContainer;
	}

	public void setTagContainer() {
		this.tagContainer = new TagContainer(selectedPoints);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Change the width of lens but it won't exceed the range determined by TagClouds 
	 * @param i, if it's positive then width will increase, vise versa.
	 */
	public void changeWidth(int i) {
		// Get the old origin's coordinates
		double oldOriginX = boundingRect.getX() + width / 2;
		double oldOriginY = boundingRect.getY() + width / 2;
		// Renew width
		if (width >= (int)TagClouds.MIN_RADIUS && width <= (int)TagClouds.MAX_RADIUS) {
			int temp = (int) (width * (i * 0.25 + 1));
			if (temp >= (int)TagClouds.MIN_RADIUS && temp <= (int)TagClouds.MAX_RADIUS)  {
				width = temp;
				// Set new bounding rectangle
				renewBoundingRect(oldOriginX, oldOriginY);
			}
		}
	}
	
	public Rectangle2D getBoundingRect() {
		return boundingRect;
	}

	public void setBoundingRect(Rectangle2D boundingRect) {
		this.boundingRect = boundingRect;
	}
	
	public void moveLens(int x, int y) {
		renewBoundingRect(x * 1.0D, y * 1.0D);
	}
	
	/**
	 * Create a new bounding rectangle
	 * @param x, X position of the origin of the new bounding rectangle 
	 * @param y, Y position of the origin of the new bounding rectangle 
	 */
	public void renewBoundingRect(double x, double y) {
		this.boundingRect = new ThreadsafeRectangle2D(x - (width / 2), y - (width / 2), width, width);
	}
	
	public void renewTags(LinkedList<InfmaTreePoint> selectedPoints) {
		this.selectedPoints.clear();
		this.selectedPoints = selectedPoints;
		this.tagContainer = new TagContainer(selectedPoints);
	}

}
