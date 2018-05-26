package de.unistuttgart.treequest.app.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

public class InfmaTree {

	private InfmaTreeNode root;
	private InfmaTreeNode currentSubRoot;
	private InfmaTreeNode selectedNode;// 默认为最新相关节点，可通过clicked改变
	private InfmaTreePoint selectedPoint;
	private InfmaTreeNode relNode;
	private InfmaTreeNode irrelNode;
	private int depth;

	// Constructor
	public InfmaTree(InfmaTreeNode root) {
		this.root = root;
		this.currentSubRoot = root;
		this.selectedNode = root;
		this.depth = 1;
	}

	public InfmaTreeNode getCurrentSubRoot() {
		synchronized (this) {
			return currentSubRoot;
		}

	}

	// Setters and Getters
	public int getDepth() {
		synchronized (this) {
			return depth;
		}
	}

	public InfmaTreeNode getRoot() {
		synchronized (this) {
			return root;
		}
	}

	public InfmaTreeNode getSelectedNode() {
		synchronized (this) {
			return selectedNode;
		}
	}

	public InfmaTreePoint getSelectedPoint() {
		synchronized (this) {
			return selectedPoint;
		}
	}

	public void setCurrentSubRoot(InfmaTreeNode selectedNode) {
		synchronized (this) {
			this.currentSubRoot = selectedNode;
		}
	}

	public void setDepth(int depth) {
		synchronized (this) {
			this.depth = depth;
		}
	}

	public void setRoot(InfmaTreeNode root) {
		synchronized (this) {
			this.root = root;
			this.setSelectedNode(root);
			this.setCurrentSubRoot(root);
		}
	}

	public void setSelectedNode(InfmaTreeNode selectedNode) {
		synchronized (this) {
			this.selectedNode = selectedNode;
		}
	}

	public void setSelectedPoint(InfmaTreePoint selectedPoint) {
		synchronized (this) {
			this.selectedPoint = selectedPoint;
		}
	}

	public void selectNode(int x, int y) {
		Point2D mousePoint = new Point2D.Double(x, y);
		synchronized (this) {
			selectedNode = findSelectedNode(currentSubRoot, mousePoint);
		}
	}

	public void selectPoint(int x, int y) {
		Point2D mousePoint = new Point2D.Double(x, y);

		synchronized (this) {
			selectedPoint = null;
			selectedPoint = findSelectedPoint(selectedNode, mousePoint);

			selectedPoint = findSelectedPoint(selectedNode, mousePoint);
		}
	}

	public LinkedList<InfmaTreePoint> findLensSelPoints(Rectangle2D boundingBox) {
		LinkedList<InfmaTreePoint> points = new LinkedList<>();
		synchronized (selectedNode) {
			for (InfmaTreePoint point : selectedNode.getPoints()) {
				if (boundingBox.intersects(point.getCurrentBounds())) {
					points.add(point);
				}
			}
		}
		return points;
	}

	public LinkedList<InfmaTreePoint> findPoints(InfmaTreeNode subroot, LinkedList<InfmaTreePoint> points) {
		synchronized (subroot) {
			if (subroot.getRightChild() == null && subroot.getLeftChild() == null) {
				for (InfmaTreePoint point : subroot.getPoints()) {
					if (point.getRelB() == 1D || point.getRelC() == 1D) {
						points.add(point);
					}
				}
			} else {
				findPoints(subroot.getLeftChild(), points);
				findPoints(subroot.getRightChild(), points);
			}
			return points;
		}
	}

	public LinkedList<InfmaTreePoint> findPoints(InfmaTreeNode relNode, InfmaTreeNode irrelNode,
			LinkedList<InfmaTreePoint> points) {

		for (InfmaTreePoint point : relNode.getPoints()) {
			if (point.getRelB() == 1D || point.getRelC() == 1D) {
				points.add(point);
			}
		}
		for (InfmaTreePoint point : irrelNode.getPoints()) {
			if (point.getRelB() == 1D || point.getRelC() == 1D) {
				points.add(point);
			}
		}
		return points;

	}

	public void explore() {
		LinkedList<InfmaTreePoint> points = new LinkedList<>();
		synchronized (selectedNode) {
			selectedNode.setExplored(true);
			//findPoints(currentSubRoot, points);
			findPoints(relNode,irrelNode, points);
			selectedNode.setPoints(points);
			selectedNode.setNDReport();
			selectedNode.setTagContainer();
		}
	}

	public void insert(InfmaTreeNode left, InfmaTreeNode right) {
		synchronized (this) {
			this.depth++;
			this.selectedNode.setLeftChild(left);
			this.selectedNode.setRightChild(right);
			left.setLevel(depth);
			right.setLevel(depth);
			this.selectedNode = this.getSelectedNode().getRightChild();
		}
	}

	private InfmaTreeNode findSelectedNode(InfmaTreeNode subroot, Point2D mousePoint) {
		synchronized (subroot) {
			if (subroot.getRightChild() == null || subroot.getLeftChild() == null
					|| subroot.getCurrentBounds().getWidth() < 100 || subroot.getCurrentBounds().getHeight() < 100) {
				subroot.setSelected(true);
				return subroot;
			} else {
				if (subroot.getLeftChild().getCurrentBounds().contains(mousePoint)) {
					return findSelectedNode(subroot.getLeftChild(), mousePoint);
				} else if (subroot.getRightChild().getCurrentBounds().contains(mousePoint)) {
					return findSelectedNode(subroot.getRightChild(), mousePoint);
				}
			}
			return null;
		}
	}

	private InfmaTreePoint findSelectedPoint(InfmaTreeNode selectedNode, Point2D mousePoint) {
		synchronized (selectedNode) {
			for (InfmaTreePoint point : selectedNode.getPoints()) {
				if (point.getCurrentBounds().contains(mousePoint)) {
					return point;
				}
			}
			return null;
		}
	}

	public InfmaTreeNode getRelNode() {
		return relNode;
	}

	public void setRelNode(InfmaTreeNode relNode) {
		this.relNode = relNode;
	}

	public InfmaTreeNode getIrrelNode() {
		return irrelNode;
	}

	public void setIrrelNode(InfmaTreeNode irrelNode) {
		this.irrelNode = irrelNode;
	}

}
