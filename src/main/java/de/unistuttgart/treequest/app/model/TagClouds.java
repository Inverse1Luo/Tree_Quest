package de.unistuttgart.treequest.app.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TagClouds {

	public final static String FONT_NAME = "Dialog";
	public final static double FONT_MINSIZE = 10;
	public final static double FONT_MAXSIZE = 30;

	public final static double MAX_RADIUS = 70;
	public final static double MIN_RADIUS = 4;

	private static double upperBound = 30;

	private synchronized static boolean collision(TagWord tw2, TagWord collisionCache, Rectangle2D boundingRect,
			boolean inside, Collection<TagWord> tagLayout, Collection<Polygon> avoid) {

		if ((collisionCache != null) && collisionCache.bb.intersects(tw2.bb)) {
			return true;
		}

		if ((inside && !boundingRect.contains(tw2.bb)) || (!inside && boundingRect.intersects(tw2.bb))) {
			return true;
		}

		if (avoid != null) {
			for (final Polygon polygon : avoid) {
				if (polygon.intersects(tw2.bb)) {
					return true;
				}
			}
		}

		for (final TagWord tagWord : tagLayout) {
			if ((tagWord != tw2) && (tagWord.bb != null)) {
				if (tagWord.bb.intersects(tw2.bb)) {
					collisionCache = tagWord;
					return true;
				}
			}
		}

		return false;
	}

	public static Collection<TagWord> createTagCloud(Graphics2D g, Rectangle2D boundingRect, boolean inside,
			List<String> tags, Map<String, Double> weights, Map<String, Point2D.Double> coordinates, double modifier,
			boolean absoluteFontMapping, Collection<Polygon> avoid, Collection<TagWord> existing) {
		Collection<TagWord> tagLayout = new LinkedList<>();
		double maxW, minW =0;
		modifier = 1;
		if (existing != null) {
			tagLayout = existing;
		}
		if(weights.size() > 0) {
		Collection<Double> ws = weights.values();
		Object[] wss = ws.toArray();
		Arrays.sort(wss);
		minW = (double) wss[0];
		maxW = (double) wss[wss.length - 1];
		modifier = maxW - minW;
		}
		for (final String tag : tags) {
			if (Charset.forName("US-ASCII").newEncoder().canEncode(tag)) {
				TagWord tw;
				tw = new TagWord(tag, generateFont(Math.sqrt(Math.sqrt(((weights.get(tag) - minW) / modifier)))));
				g.setFont(tw.font);
				final double width = g.getFontMetrics(g.getFont()).getStringBounds(tag, g).getWidth();
				final double height = g.getFontMetrics(g.getFont()).getStringBounds(tag, g).getHeight();

				if (coordinates == null) {
					tw.bb = new Rectangle2D.Double(0, 0, width * 1.5, height * 1.5);
				} else {
					tw.bb = new Rectangle2D.Double(coordinates.get(tag).getX(), coordinates.get(tag).getY(),
							width * 1.5, height * 1.5);
				}

				if (simplePlacement(tw, boundingRect, inside, tagLayout, avoid)) {
					tagLayout.add(tw);
				}
			}
		}

		return tagLayout;
	}

	public static Font generateFont(double weight) {

		final double fontweight = FONT_MAXSIZE * weight;
		return new Font(FONT_NAME, Font.PLAIN,
				(int) Math.round(Math.max(FONT_MINSIZE, Math.min(FONT_MAXSIZE, fontweight))));

	}

	public static Rectangle2D generatePlacement(Graphics2D g, String term, double weight, Point2D position) {
		final Font font = generateFont(weight);
		final double width = g.getFontMetrics(font).getStringBounds(term, g).getWidth();
		final double height = g.getFontMetrics(font).getStringBounds(term, g).getHeight();
		return new Rectangle2D.Double(position.getX() - (width / 2), position.getY() - (height / 2), width, height);
	}

	public static Rectangle2D generatePlacement(Graphics2D g, String term, Font font, Point2D position) {
		final double width = g.getFontMetrics(font).getStringBounds(term, g).getWidth();
		final double height = g.getFontMetrics(font).getStringBounds(term, g).getHeight();
		return new Rectangle2D.Double(position.getX() - (width / 2), position.getY() - (height / 2), width, height);
	}

	public static void renderTagCloud(Graphics2D g, Collection<TagWord> tagLayout, Color overrideColor) {

		for (final TagWord tagWord : tagLayout) {

			if (overrideColor != null) {
				g.setColor(overrideColor);
			} else {
				g.setColor(tagWord.color);
			}

			g.setFont(tagWord.font);
			g.drawString(tagWord.term, (float) tagWord.bb.getX() + (float) (tagWord.bb.getWidth() / 20f),
					(float) tagWord.bb.getMaxY() - g.getFontMetrics(g.getFont()).getDescent());

		}

	}

	public static void renderTagCloud(Graphics2D g, Rectangle2D boundingRect, boolean inside, List<String> tags,
			Map<String, Double> weights, Collection<Polygon> avoid) {
		renderTagCloud(g, createTagCloud(g, boundingRect, inside, tags, weights, null, 1d, true, avoid, null), null);
	}

	public static void renderTagCloud(Graphics2D g, Rectangle2D boundingRect, boolean inside, List<String> tags,
			Map<String, Double> weights, Map<String, Point2D.Double> coordinates, Collection<Polygon> avoid) {
		renderTagCloud(g, createTagCloud(g, boundingRect, inside, tags, weights, coordinates, 1d, true, avoid, null),
				null);
	}

	private static Point2D rotate(Point2D center, double angle, double Radius) {

		final Point2D p = new Point2D.Double(0, 0);

		final double iangle = Math.toRadians(angle);

		final double x = center.getX() + Math.round(Radius * Math.cos(iangle));
		final double y = center.getY() + Math.round(Radius * Math.sin(iangle));

		p.setLocation(x, y);
		return p;

	}

	public static void setUpperBound(double max) {
		upperBound = max;
	}

	public synchronized static boolean simplePlacement(TagWord tw2, Rectangle2D boundingRect, boolean inside,
			Collection<TagWord> tagLayout, Collection<Polygon> avoid) {

		final TagWord collisionCache = null;
		// setup
		final double maxradius = (inside
				? Math.sqrt(Math.pow(boundingRect.getWidth() / 2, 2) + Math.pow(boundingRect.getHeight() / 2, 2))
				: MAX_RADIUS);
		final double out = 5;
		final double forward = 5;
		final double startradius = (inside ? 0d : (boundingRect.getWidth() / 2) * Math.sqrt(2));
		final Point2D p0 = new Point2D.Double(boundingRect.getCenterX() - (tw2.bb.getWidth() / 2),
				boundingRect.getCenterY() - (tw2.bb.getHeight() / 2));

		boolean collision = true;
		for (double radius = startradius; radius < maxradius; radius += out) {
			final double startangle = 0;
			for (double angle = startangle; angle < (startangle + 360); angle += forward) {
				final Point2D p = rotate(p0, angle, radius);
				tw2.bb.setFrame(p.getX(), p.getY(), tw2.bb.getWidth(), tw2.bb.getHeight());

				if (!collision(tw2, collisionCache, boundingRect, inside, tagLayout, avoid)) {
					collision = false;
					break;
				}
			}
			if (!collision) {
				break;
			}
		}

		return !collision;
	}

}
