package de.unistuttgart.treequest.app.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Stack;

import javax.swing.JPanel;

import de.unistuttgart.treequest.app.model.threadsafe.ThreadsafeRectangle2D;
import de.unistuttgart.vis.tweet.Tweet;


public class InfmaTreemap extends JPanel {

	private InfmaTree infmaTree;
	private InfmaTreeLens lens;
	private Stack<InfmaTreeNode> zoomHistory = new Stack<>();
	private boolean showTagCloud = false;


	public InfmaTreemap(InfmaTree infmaTree) {
		super();
		this.infmaTree = infmaTree;
	}

	public void setInfmaTree(InfmaTree theTree) {
		this.infmaTree = theTree;
	}

	public InfmaTree getInfmaTree() {
		return this.infmaTree;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		g.setStroke(new BasicStroke(3));
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, this.getWidth(), this.getHeight());

		if (infmaTree != null) {
			infmaTree.getCurrentSubRoot().paint(g, 0, 0, this.getWidth(), this.getHeight(), 1, showTagCloud);
			if (infmaTree.getSelectedNode() != null) {
				g.setColor(new Color(200, 200, 200, 128));
				g.fill(infmaTree.getSelectedNode().getCurrentBounds());
			}

			if (infmaTree.getSelectedPoint() != null) {
				
				Rectangle2D pointRec = infmaTree.getSelectedPoint().getCurrentBounds();
				g.setColor(Color.RED);
				g.fillRect((int) pointRec.getX(), (int) pointRec.getY(), (int) pointRec.getWidth(),
						(int) pointRec.getHeight());
			}
		}

		if (lens != null) {
			paintLens(g);
		}
	}

	private void paintLens(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.draw(lens.getBoundingRect());
		g.setColor(new Color(128, 128, 128, 128));
		g.fill(lens.getBoundingRect());

		TagClouds.renderTagCloud(g, lens.getBoundingRect(), false, lens.getTagContainer().getTopTerms(),
				lens.getTagContainer().getTermWeights(), null);
	}

	public void zoomIn() {

		if (infmaTree.getSelectedNode() != null) {
			zoomHistory.push(infmaTree.getSelectedNode());
			infmaTree.setCurrentSubRoot(infmaTree.getSelectedNode());
		}

		repaint();
	}

	public void zoomOut() {

		if (infmaTree.getSelectedNode() != null) {
			infmaTree.setSelectedNode(null);
		}

		if (zoomHistory.isEmpty()) {
			infmaTree.setCurrentSubRoot(infmaTree.getRoot());
		} else {
			InfmaTreeNode newSubRoot = zoomHistory.pop();
			infmaTree.setCurrentSubRoot(newSubRoot);
		}

		repaint();
	}

	public InfmaTreeLens getLens() {
		return lens;
	}

	public void setLens(InfmaTreeLens lens) {
		this.lens = lens;
	}

	public boolean isShowTagCloud() {
		return showTagCloud;
	}

	public void setShowTagCloud(boolean showTagCloud) {
		this.showTagCloud = showTagCloud;
	}

}
