package de.unistuttgart.treequest.app.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

public class TagWord {
	public TagWord(String term, Font font) {
		super();
		this.term = term;
		this.font = font;
	}

	public Rectangle2D bb;
	public Color color = Color.BLACK;
	public String term;
	public Font font = null;
}
