package de.unistuttgart.treequest.app.model;

import javax.swing.JTextPane;

public class InfmaTreeNodeDetail extends JTextPane {

	public InfmaTreeNodeDetail() {

		this.setText("init");
	}

	public void report(InfmaTreeNode node) {
		if (node != null) {
			String report = "";
			report += "******************hashtags*********************" +"\n";
			for (String[] s : node.getTable()) {
				if (s[0] != null) {
					report += s[0] + "\n";
				}

			}
			report += "******************usermentions****************" +"\n";
			for (String[] s : node.getTable()) {
				if (s[1] != null) {
					report += s[1] + "\n";
				}
			}
			report += "******************urls***************************" +"\n";
			for (String[] s : node.getTable()) {
				if (s[2] != null) {
					report += s[2] + "\n";
				}
			}
			this.setText(report);
		}
	}
}
