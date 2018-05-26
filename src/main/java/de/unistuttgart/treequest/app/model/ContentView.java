package de.unistuttgart.treequest.app.model;

import java.awt.BorderLayout;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.unistuttgart.vis.tweet.Tweet;

public class ContentView extends JPanel {

	public ContentTable contentTable;
	private JScrollPane scrollPane;
	private final JLabel resultLabel;
	private Integer dataCount = new Integer(0);
	private InfmaTree theTree;

	public ContentView(InfmaTree theTree) {
		this.theTree = theTree;
		this.setLayout(new BorderLayout());
		contentTable = new ContentTable(this);
		scrollPane = new JScrollPane(contentTable);
		add(scrollPane, BorderLayout.CENTER);
		resultLabel = new JLabel("0 Results");
		add(resultLabel, BorderLayout.SOUTH);
	}

	public InfmaTree getTree() {
		return theTree;
	}

	public void setTree(InfmaTree theTree) {
		this.theTree = theTree;
	}

	public void updateContentView() {
		repaint();
		scrollPane.repaint();
		resultLabel.setText(dataCount.toString() + " selected ");
	}

	public void update(InfmaTreeNode node) {

		if (node != null) {
			this.remove(scrollPane);
			contentTable = new ContentTable(this);
			scrollPane = new JScrollPane(contentTable);
			add(scrollPane, BorderLayout.CENTER);

			contentTable.setDocuments(node.getTweets());
			dataCount = node.getTweets().size();
		}
		updateContentView();
	}
	
	public void update(InfmaTreePoint point) {
		Collection<Tweet> tweets = new LinkedList<>();
		tweets.add(point.getTweet());
		contentTable.setDocuments(tweets);
		dataCount = tweets.size();
		updateContentView();
	}

	public void update(LinkedList<InfmaTreePoint> points) {
		Collection<Tweet> tweets = new LinkedList<>();
		for (InfmaTreePoint point : points) {
			tweets.add(point.getTweet());
		}
		contentTable.setDocuments(tweets);
		dataCount = tweets.size();
		updateContentView();
		
	}
}
