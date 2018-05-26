package de.unistuttgart.treequest.app.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unistuttgart.vis.tweet.Tweet;

public class InfmaTreePoint {

	private double xr, yr;
	private double x, y;
	double decVal;// classifier defined position
	private double Drange;
	private Tweet tweet;
	private Rectangle2D currentBounds = null;
	private List<Double> relevance = Collections.synchronizedList(new ArrayList<Double>());
	private double relC = 0;// relevance decided by classifier
	private double relQ = 0;// relevance decided by query
	private double relB = 0;// relevance decided by user

	public InfmaTreePoint(Tweet tweet, double relQ, Long dRange) {
		this(tweet, relQ);
		this.Drange = dRange;
	}

	public InfmaTreePoint(Tweet tweet, double relQ) {
		this.tweet = tweet;
		this.relQ = relQ;

		xr = Math.random();
		yr = Math.random();
	}

	// public InfmaTreePoint(Tweet tweet, Long dRange) {
	// this(tweet);
	// this.Drange = dRange;
	// }
	//
	// public InfmaTreePoint(Tweet tweet) {
	// this.tweet = tweet;
	// xr = Math.random();
	// yr = Math.random();
	//
	// }

	public Rectangle2D getCurrentBounds() {
		synchronized (this) {
			return currentBounds;
		}
	}

	public void setCurrentBounds(Rectangle2D currentBounds) {
		synchronized (this) {
			this.currentBounds = currentBounds;
		}
	}

	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	public void setX(double x) {
		synchronized (this) {
			this.x = x;
		}
	}

	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	public void setY(double y) {
		synchronized (this) {
			this.y = y;
		}
	}

	public Tweet getTweet() {
		synchronized (this) {
			return tweet;
		}
	}

	public void setTweet(Tweet tweet) {
		synchronized (this) {
			this.tweet = tweet;
		}
	}

	public List<Double> getRelevance() {
		synchronized (this) {
			return relevance;
		}
	}

	public void setRelevance(List<Double> relevance) {
		synchronized (this) {
			this.relevance = relevance;
		}
	}

	public double getRelC() {
		synchronized (this) {
			return relC;
		}
	}

	public void setRelC(double relC) {
		synchronized (this) {
			this.relC = relC;
		}
	}

	public double getRelQ() {
		synchronized (this) {
			return relQ;
		}
	}

	public void setRelQ(double relQ) {
		synchronized (this) {
			this.relQ = relQ;
		}
	}

	public double getRelB() {
		synchronized (this) {
			return relB;
		}
	}

	public void setRelB(double relB) {
		synchronized (this) {
			this.relB = relB;
		}
	}

	public double getDrange() {
		synchronized (this) {
			return Drange;
		}
	}

	public void setDrange(double drange) {
		synchronized (this) {
			this.Drange = drange;
		}
	}

	public boolean isRelChanged() {
		synchronized (this) {
			return relC != relQ;
		}
	}

	public double getDecVal() {
		synchronized (this) {
			return decVal;
		}
	}

	public void setDecVal(double decVal) {
		synchronized (this) {
			this.decVal = decVal;
		}
	}

	private void generateXY(int xpos, int ypos, int width, int height, int nodeLevel, double maxDecVal,
			double minDecVal, double decValRange, double minDate, double dateRange) {
		if ((nodeLevel % 2) == 0) {// 垂直分割线
			if (relQ == -1) { // 分割线左
				if (minDate != 0 && dateRange != 0) { // 纵坐标为时间
					y = ypos + (int) (((tweet.getDateStamp() - minDate) / dateRange) * height);// date

				} else {
					y = ypos + (int) (yr * height);// 随机纵坐标
				}
				if (maxDecVal != 0 || minDecVal != 0 || decValRange != 0) { // 横坐标为置信值
					x = xpos + width - (int) (((-decVal + maxDecVal) / decValRange) * width);
				} else {
					x = xpos + (int) (xr * width);
				}
				if((xpos+width)<x || x< xpos) {
					System.err.println("[ " + xpos + ", " + (xpos+width)+", " + "]" + "\n" + "[ " + x + "]");
					System.err.println("x**********1***********");
				}
				if((ypos+height)<y || y< ypos) {
					System.err.println("["+ ypos+", " + (ypos+height) + "]" + "\n" + "[ " + y+"]");
					System.err.println("y**********1***********");
				}
			} else { // 分割线左

				if (minDate != 0 && dateRange != 0) {
					y = ypos + (int) (((tweet.getDateStamp() - minDate) / dateRange) * height);
				} else {
					y = ypos + (int) (yr * height);
				}

				if (maxDecVal != 0 || minDecVal != 0 || decValRange != 0) {
					x = xpos + (int) (((decVal - minDecVal) / decValRange) * width);

				} else {
					x = xpos + (int) (xr * width);
				}
				if((xpos+width)<x || x< xpos) {
					System.err.println("[ " + xpos + ", " + (xpos+width)+", " + "]" + "\n" + "[ " + x + "]");
					System.err.println("x**********2***********");
				}
				if((ypos+height)<y || y< ypos) {
					System.err.println("["+ ypos+", " + (ypos+height) + "]" + "\n" + "[ " + y+"]");
					System.err.println("y**********2***********");
				}
			}
		} else { // 水平分割线
			if (relQ == -1) { // 上

				if (minDate != 0 && dateRange != 0) {
					x = xpos + (int) (((tweet.getDateStamp() - minDate) / dateRange) * width);
				} else {
					x = xpos + (int) (xr * width);
				}
				if (maxDecVal != 0 || minDecVal != 0 || decValRange != 0) {

					// y = ypos + (int) ((((-decVal + maxDecVal) / decValRange) * height));
					
					// y = ypos + height - (int) (((-decVal + maxDecVal) / decValRange) * height);
					y = ypos + (int)(((decVal-minDecVal)/ decValRange)*height);
				} else {
					y = ypos + height - (int) ((yr * height));
				}
				if((xpos+width)<x || x< xpos) {
					System.err.println("[ " + xpos + ", " + (xpos+width)+", " + "]" + "\n" + "[ " + x + "]");
					System.err.println("x**********3***********");
				}
				if((ypos+height)<y || y< ypos) {
					System.err.println("["+ ypos+", " + (ypos+height) + "]" + "\n" + "[ " + y+"]");
					System.err.println("y**********3***********");
				}
			} else {

				if (minDate != 0 && dateRange != 0) {
					x = xpos + (int) (((tweet.getDateStamp() - minDate) / dateRange) * width);
				} else {
					x = xpos + (int) (xr * width);
				}

				if (maxDecVal != 0 || minDecVal != 0 || decValRange != 0) {
					// y = ypos + (int) ((((decVal) / decValRange - minDecVal) * height));
					y = ypos + (int) ((((decVal - minDecVal) / decValRange) * height));
				} else {
					y = ypos + (int) ((yr * height));
				}
			}
			if((xpos+width)<x || x< xpos) {
				System.err.println("[ " + xpos + ", " + (xpos+width)+", " + "]" + "\n" + "[ " + x + "]");
				System.err.println("x**********4***********");
			}
			if((ypos+height)<y || y< ypos) {
				System.err.println("["+ ypos+", " + (ypos+height) + "]" + "\n" + "[ " + y+"]");
				System.err.println("y**********4***********");
			}
		}

	}

	public void paint(Graphics2D g, Rectangle2D nodeBounds, int nodeLevel, long minDate, long dateRange,
			double minDecVal, double maxDecVal, double decValRange) {

		int xpos, ypos, width, height;
		xpos = (int) nodeBounds.getX();
		ypos = (int) nodeBounds.getY();
		width = (int) nodeBounds.getWidth();
		height = (int) nodeBounds.getHeight();
		generateXY(xpos, ypos, width, height, nodeLevel, maxDecVal, minDecVal, decValRange, minDate, dateRange);

		if (relB == 1) {
			g.setColor(Color.MAGENTA);
		} else if (relB == -1) {
			g.setColor(Color.CYAN);
		} else if (relC == 1) {
			g.setColor(new Color(244, 164, 96));
		} else if (relC == -1) {
			g.setColor(new Color(0, 0, 255, 150));
		} else {
			g.setColor(Color.GRAY);
		}

		if (relC != 0 && relC != relQ) {
			g.fillOval((int) x, (int) y, 3, 3);
		} else {
			g.fillRect((int) x, (int) y, 3, 3);
		}

		currentBounds = new Rectangle2D.Double(x, y, 3, 3);
	}

	public void paintTSNE(Graphics2D g, Rectangle2D nodeBounds) {
		int xpos, ypos, width, height;
		xpos = (int) nodeBounds.getX();
		ypos = (int) nodeBounds.getY();
		width = (int) nodeBounds.getWidth();
		height = (int) nodeBounds.getHeight();
		g.fillRect((int) (xpos + x * width), (int) (ypos + y * height), 3, 3);
		currentBounds = new Rectangle2D.Double((int) (xpos + x * width), (int) (ypos + y * height), 3, 3);

	}

}
