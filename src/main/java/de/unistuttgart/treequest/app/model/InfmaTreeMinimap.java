package de.unistuttgart.treequest.app.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

public class InfmaTreeMinimap extends JPanel {

	private ExecutorService threadPool = Executors.newFixedThreadPool(1);

	private final Color DEFAULT_COLOR = Color.GRAY;

	private void paintNode(Graphics2D g, InfmaTreeNode node, int relativeNodeLevel, int xpos, int ypos, int width,
			int height) {

		if (node == theTree.getSelectedNode()) {
			g.setColor(new Color(200, 200, 200, 128));
			g.fillRect(xpos, ypos, width, height);
		}

		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2f));
		if (!node.isLeafNode()) {
			if (relativeNodeLevel % 2 == 0) {
				// HORIZONTAL LAYOUT
				// g.fillRect(xpos + (width / 2), ypos, 1, height);
				g.drawLine(xpos + (int) Math.round(width / 2d), ypos, xpos + (int) Math.round(width / 2d),
						ypos + height);
				paintNode(g, node.getLeftChild(), relativeNodeLevel + 1, xpos, ypos, (int) Math.round(width / 2d),
						height);
				paintNode(g, node.getRightChild(), relativeNodeLevel + 1, xpos + (int) Math.round(width / 2d), ypos,
						(int) Math.round(width / 2d), height);
			} else {
				// VERTICAL LAYOUT
				g.drawLine(xpos, ypos + (int) Math.round(height / 2d), xpos + width,
						ypos + (int) Math.round(height / 2d));
				paintNode(g, node.getLeftChild(), relativeNodeLevel + 1, xpos, ypos, width,
						(int) Math.round(height / 2d));
				paintNode(g, node.getRightChild(), relativeNodeLevel + 1, xpos, ypos + (int) Math.round(height / 2d),
						width, (int) Math.round(height / 2d));
			}

			if (node == theTree.getRoot()) {
				g.setColor(Color.BLACK);
				g.setStroke(new BasicStroke(2f));
				g.drawRect(xpos, ypos, width - 1, height - 1);
				g.setStroke(new BasicStroke(1f));
			}
		}

	}

	private void resetThreadPool() {
		threadPool.shutdownNow();
		threadPool = Executors.newFixedThreadPool(1);
	}

	@Override

	public void paint(Graphics g) {
		Graphics2D context = ((Graphics2D) g);

		context.setBackground(Color.WHITE);
		context.clearRect(0, 0, this.getWidth(), this.getHeight());
		context.setStroke(new BasicStroke(3));
		context.setColor(Color.BLACK);
		context.drawRect(0, 0, this.getWidth(), this.getHeight());

		 if (theTree != null) {
		 paintNode(context, theTree.getRoot(), 0, 0, 0, this.getWidth(),
		 this.getHeight());
		
		 }
//		resetThreadPool();
//		threadPool.submit(new Runnable() {
//
//			@Override
//			public void run() {
//				if (theTree != null) {
//					paintNode(context, theTree.getRoot(), 0, 0, 0, getWidth(), getHeight());
//				}
//			}
//		});

	}

	public InfmaTreeMinimap(InfmaTree theTree) {
		super();
		this.theTree = theTree;
	}

	private InfmaTree theTree;

	public void setTheTree(InfmaTree theTree) {
		this.theTree = theTree;
	}

}