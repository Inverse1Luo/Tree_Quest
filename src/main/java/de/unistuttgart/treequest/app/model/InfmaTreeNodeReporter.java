package de.unistuttgart.treequest.app.model;

import javax.swing.JTextPane;

public class InfmaTreeNodeReporter extends JTextPane {

	public InfmaTreeNodeReporter() {

		this.setText("init");
	}

	public void report(InfmaTreeNode node) {
		if (node != null) {
			String report = "";
			report += "Level: " + node.getLevel() + "\n";
			report += "Tweets: " + node.getTweets().size() + "\n";
			report += "#hash: " + node.getHashtagCount() + "\n";
			report += "@user: " + node.getAt_userCount() + "\n";
			report += "url: " + node.getUrlCount() + "\n";
			this.setText(report);
		}
	}
}
