package de.unistuttgart.treequest.app.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.util.List;
import java.util.Map;

import javax.xml.soap.Text;

import org.netlib.util.booleanW;
import org.netlib.util.doubleW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import de.unistuttgart.treequest.app.util.QueryHistory;
import de.unistuttgart.vis.tweet.Tweet;
import edu.stanford.nlp.util.TwoDimensionalMap.Entry;

public class InfmaTreeNode {

	private InfmaTree tweetTree;
	private InfmaTreeNode leftChild;
	private InfmaTreeNode rightChild;

	private List<InfmaTreePoint> points = Collections.synchronizedList(new LinkedList<InfmaTreePoint>());
	private TagContainer tagContainer;
	private double rel = 0;
	private List<Tweet> tweets = Collections.synchronizedList(new LinkedList<Tweet>());
	private int urlCount = 0;
	private int hashtagCount = 0;
	private int at_userCount = 0;
	private Map<String, Integer> hashtags = new HashMap<String, Integer>();
	private Map<String, Integer> usermentions = new HashMap<String, Integer>();
	private Map<String, Integer> urls = new HashMap<String, Integer>();

	private Rectangle2D currentBounds = null;
	private int level;

	private double maxDecVal, minDecVal, decValRange;
	private long maxDate, minDate, dateRange;
	private boolean useTimeRange = false;
	private boolean classified = false;
	public boolean useTSNE = false;
	private double[][] pointXYs;

	// private boolean selected = false;
	private QueryHistory queryHistory;
	private List<Long> docids = Collections.synchronizedList(new LinkedList<Long>());// 用于搜索
	private boolean explored = false;
	private double maxDelVal, minDelVal;

	// Constructor
	public InfmaTreeNode(List<Tweet> tweets, InfmaTree tweetTree, QueryHistory queryHistory, double rel) {
		this.tweets.addAll(tweets);
		this.tweetTree = tweetTree;
		this.queryHistory = queryHistory;
		this.rel = rel;
		setPoints(tweets);
		tagContainer = new TagContainer(points);
	}

	// Getters and Setters
	public Rectangle2D getCurrentBounds() {
		synchronized (this) {
			return currentBounds;
		}
	}

	public Collection<Long> getDocids() {
		synchronized (this) {
			return docids;
		}
	}

	public boolean getExplored() {
		return explored;
	}

	public InfmaTreeNode getLeftChild() {
		synchronized (this) {
			return leftChild;
		}
	}

	public int getLevel() {
		return level;
	}

	public double getMaxDelVal() {
		synchronized (this) {
			return maxDelVal;
		}
	}

	public double getMinDelVal() {
		synchronized (this) {
			return minDelVal;
		}
	}

	public long getMaxDate() {
		synchronized (this) {
			return maxDate;
		}
	}

	public long getMinDate() {
		synchronized (this) {
			return minDate;
		}
	}

	public List<InfmaTreePoint> getPoints() {
		synchronized (this) {
			return points;
		}
	}

	public QueryHistory getQueryHistory() {
		synchronized (this) {
			return queryHistory;
		}
	}

	public double getRel() {
		synchronized (this) {
			return rel;
		}
	}

	public InfmaTreeNode getRightChild() {
		synchronized (this) {
			return rightChild;
		}
	}

	public TagContainer getTagContainer() {
		synchronized (this) {
			return tagContainer;
		}
	}

	public void setExplored(boolean explored) {
		this.explored = explored;
	}

	public void setLeftChild(InfmaTreeNode leftChild) {
		synchronized (this) {
			this.leftChild = leftChild;
		}
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxDelVal(double maxDelVal) {
		synchronized (this) {
			this.maxDelVal = maxDelVal;
		}
	}

	public void setMinDelVal(double minDelVal) {
		synchronized (this) {
			this.minDelVal = minDelVal;
		}
	}

	public void setMaxDate(long maxDate) {
		synchronized (this) {
			this.maxDate = maxDate;
		}
	}

	public void setMinDate(long minDate) {
		synchronized (this) {
			this.minDate = minDate;
		}
	}

	String[] hashStrings = new String[50];
	String[] urlStrings = new String[50];
	String[] userStrings = new String[50];
	String[][] strings = new String[50][3];

	public void setPoints(List<Tweet> tweets) {
		Long drange = calcDRange(tweets);
		synchronized (this) {
			if (tweets.size() != 0) {
				for (Tweet tweet : tweets) {
					InfmaTreePoint point = new InfmaTreePoint(tweet, rel, drange);
					// InfmaTreePoint point = new InfmaTreePoint(tweet, drange);
					this.points.add(point);
					if (tweet.getAt_user().length() != 0) {
						String[] texts = tweet.getAt_user().split(" ");
						for (String text : texts) {
							int count = 1;
							if (usermentions.containsKey(text)) {
								count = usermentions.get(text) + 1;
							}
							usermentions.put(text, count);
							at_userCount++;
						}
					}
					if (tweet.getHashtag().length() != 0) {
						String[] texts = tweet.getHashtag().split(" ");
						for (String text : texts) {
							int count = 1;
							if (hashtags.containsKey(text)) {
								count = hashtags.get(text) + 1;
							}
							hashtags.put(text, count);
							hashtagCount++;
						}
					}
					if (tweet.getUrl().length() != 0) {
						String[] texts = tweet.getUrl().split(" ");
						for (String text : texts) {
							int count = 1;
							if (urls.containsKey(text)) {
								count = urls.get(text) + 1;
							}
							urls.put(text, count);
							urlCount++;
						}
					}
				}
			}
		}
		this.sort(hashtags, hashStrings);
		this.sort(usermentions, userStrings);
		this.sort(urls, urlStrings);
		createTabel(hashStrings, userStrings, urlStrings, strings);
	}

	public String[][] getTable() {
		return strings;

	}
	public void setNDReport() {
		for(InfmaTreePoint point: points) {
			Tweet tweet = point.getTweet();
			if (tweet.getAt_user().length() != 0) {
				String[] texts = tweet.getAt_user().split(" ");
				for (String text : texts) {
					int count = 1;
					if (usermentions.containsKey(text)) {
						count = usermentions.get(text) + 1;
					}
					usermentions.put(text, count);
					at_userCount++;
				}
			}
			if (tweet.getHashtag().length() != 0) {
				String[] texts = tweet.getHashtag().split(" ");
				for (String text : texts) {
					int count = 1;
					if (hashtags.containsKey(text)) {
						count = hashtags.get(text) + 1;
					}
					hashtags.put(text, count);
					hashtagCount++;
				}
			}
			if (tweet.getUrl().length() != 0) {
				String[] texts = tweet.getUrl().split(" ");
				for (String text : texts) {
					int count = 1;
					if (urls.containsKey(text)) {
						count = urls.get(text) + 1;
					}
					urls.put(text, count);
					urlCount++;
				}
			}
		}
		this.sort(hashtags, hashStrings);
		this.sort(usermentions, userStrings);
		this.sort(urls, urlStrings);
		createTabel(hashStrings, userStrings, urlStrings, strings);	
	}

	private void createTabel(String[] s1, String[] s2, String[] s3, String[][] strings) {
		for (int i = 0; i < strings.length; i++) {
			strings[i][0] = s1[i];
			strings[i][1] = s2[i];
			strings[i][2] = s3[i];
		}
	}

	private void sort(Map<String, Integer> map, String[] strings) {
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(java.util.Map.Entry<String, Integer> o1, java.util.Map.Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		int i = 0;
		for (Map.Entry<String, Integer> mapping : list) {
			if (mapping.getValue() > 1) {
				strings[i] = mapping.getKey() + ":" + mapping.getValue();
				// System.out.println(i+ mapping.getKey() + ":" + mapping.getValue());
			}
			i++;
			if (i >= 50) {
				break;
			}
		}
	}

	public void setPoints(LinkedList<InfmaTreePoint> points) {
		this.tweets = Collections.synchronizedList(new LinkedList<Tweet>());
		synchronized (this) {
			this.points = points;
			for (InfmaTreePoint point : points) {
				tweets.add(point.getTweet());
				docids.add(point.getTweet().getTweetID());
			}
		}
		calcDecValRange();
	}

	public void setRel(double rel) {
		synchronized (this) {
			this.rel = rel;
		}
	}

	public void calcDateRange() {
		int i = 0;
		for (Tweet tweet : tweets) {
			maxDate = tweet.getDateStamp() > maxDate ? tweet.getDateStamp() : maxDate;
			if (i == 0) {
				minDate = maxDate;
				i++;
			}
			minDate = tweet.getDateStamp() < minDate ? tweet.getDateStamp() : minDate;
		}
		dateRange = maxDate - minDate;
	}

	public void setRightChild(InfmaTreeNode rightChild) {
		synchronized (this) {
			this.rightChild = rightChild;
		}
	}

	public void setTagContainer() {
		synchronized (this) {
			this.tagContainer = new TagContainer(points);
		}
	}

	public List<Tweet> getTweets() {
		synchronized (this) {
			return tweets;
		}
	}

	public void setTweets(List<Tweet> tweets) {
		synchronized (this) {
			this.tweets = tweets;
		}
	}

	public void setSelected(boolean selected) {
		// this.selected = selected;
		synchronized (this) {
			this.tweetTree.setSelectedNode(this);
		}
	}

	public boolean isLeafNode() {
		if (this.leftChild == null || this.rightChild == null) {
			return true;
		} else {
			return false;
		}
	}

	public double calcCRange() {
		synchronized (this) {
			for (InfmaTreePoint point : points) {
				if (point.getDecVal() != 0.0) {
					maxDelVal = point.getDecVal() > maxDelVal ? point.getDecVal() : maxDelVal;
					minDelVal = point.getDecVal() < minDelVal ? point.getDecVal() : minDelVal;
				}
			}
			return maxDelVal - minDelVal;
		}
	}
	
	public void calcDecValRange() {
		for (InfmaTreePoint point : points) {
			if (point.decVal != 0.0) {
				maxDecVal = point.decVal > maxDecVal ? point.decVal : maxDecVal;
				minDecVal = point.decVal < minDecVal ? point.decVal : minDecVal;
			}
		}
		decValRange = maxDecVal - minDecVal;
	}

	private Long calcDRange(List<Tweet> tweets) {
		int i = 0;
		synchronized (this) {
			for (Tweet tweet : tweets) {
				maxDate = tweet.getDateStamp() > maxDate ? tweet.getDateStamp() : maxDate;
				if (i == 0) {
					minDate = maxDate;
					i++;
				}
				minDate = tweet.getDateStamp() < minDate ? tweet.getDateStamp() : minDate;
			}
			return maxDate - minDate;
		}
	}

	public void paint(Graphics2D g, int xpos, int ypos, int width, int height, int relativeNodeLevel,
			boolean showTagCloud) {
		currentBounds = new Rectangle2D.Double(xpos, ypos, width, height);
		
		double ratio = 0.5d;
		if (isLeafNode() || width <= 100 || height <= 100) {
			if (useTSNE) {
				if (pointXYs == null) {
					generateTSNEMap(g);
				}
				for (InfmaTreePoint point : points) {
					point.paintTSNE(g, currentBounds);
				}

			} else
				for (InfmaTreePoint point : points) {
					if (useTimeRange) {
						if (classified) {
							// calcDecValRange();
//							point.paint(g, currentBounds, relativeNodeLevel, minDate, dateRange, minDecVal, maxDecVal,
//									decValRange);
							point.paint(g, currentBounds, relativeNodeLevel, minDate, dateRange, minDecVal, maxDecVal,
									decValRange);
						} else {
//							point.paint(g, currentBounds, relativeNodeLevel, minDate, dateRange, 0, 0, 0);
							point.paint(g, currentBounds, relativeNodeLevel, minDate, dateRange, 0, 0, 0);
						}
					} else {
						if (classified) {
							// calcDecValRange();
//							point.paint(g, currentBounds, relativeNodeLevel, 0, 0, minDecVal, maxDecVal, decValRange);
							point.paint(g, currentBounds, relativeNodeLevel, 0, 0, minDecVal, maxDecVal, decValRange);
						} else {
//							point.paint(g, currentBounds, relativeNodeLevel, 0, 0, 0, 0, 0);
							point.paint(g, currentBounds, relativeNodeLevel, 0, 0, 0, 0, 0);
						}

					}

				}
			if (showTagCloud) {
				int xoffset = -1;
				int yoffset = -1;
				int TAGCLOUD_OFFSET = 10;
				Collection<TagWord> tagLayout = null;
				tagLayout = TagClouds.createTagCloud(g,
						new Rectangle2D.Double(xpos + xoffset + TAGCLOUD_OFFSET, ypos + yoffset + TAGCLOUD_OFFSET,
								width - (TAGCLOUD_OFFSET * 2), height - (TAGCLOUD_OFFSET * 2)),
						true, tagContainer.getTopTerms(), tagContainer.getTermWeights(), null,
						tagContainer.getTweetCount(), false, null, null);
				TagClouds.renderTagCloud(g, tagLayout, null);

			}
		} else {
			{
				if (leftChild.getExplored() || rightChild.getExplored()) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.BLACK);
				}
				if (relativeNodeLevel % 2 == 1) {
					g.drawLine(xpos + (int) Math.round(width * ratio), ypos, xpos + (int) Math.round(width * ratio),
							ypos + height);
					leftChild.paint(g, xpos, ypos, (int) Math.round(width * ratio), height, relativeNodeLevel + 1,
							showTagCloud);
					rightChild.paint(g, xpos + (int) Math.round(width * ratio), ypos, (int) Math.round(width * ratio),
							height, relativeNodeLevel + 1, showTagCloud);
				} else {
					g.drawLine(xpos, ypos + (int) Math.round(height * ratio), xpos + width,
							ypos + (int) Math.round(height * ratio));
					leftChild.paint(g, xpos, ypos, width, (int) Math.round(height * ratio), relativeNodeLevel + 1,
							showTagCloud);
					rightChild.paint(g, xpos, ypos + (int) Math.round(height * ratio), width,
							(int) Math.round(height * ratio), relativeNodeLevel + 1, showTagCloud);

				}
			}

		}
	}

	public void generateTSNEMap(Graphics2D g) {
		double minX = 0, maxX = 0, minY = 0, maxY = 0, xRange, yRange;
		pointXYs = new double[tweets.size()][];
		TsneMap pca = new TsneMap();
		pointXYs = pca.drawTsneMap(tweets);
		for (double[] pointXY : pointXYs) {

			minX = pointXY[0] < minX ? pointXY[0] : minX;
			maxX = pointXY[0] > maxX ? pointXY[0] : maxX;
			minY = pointXY[1] < minY ? pointXY[1] : minY;
			maxY = pointXY[1] > maxY ? pointXY[1] : maxY;

		}
		xRange = maxX - minX;
		yRange = maxY - minY;

		int i = 0;
		for (InfmaTreePoint point : points) {
			point.setX((pointXYs[i][0] - minX) / xRange);
			point.setY((pointXYs[i][1] - minY) / yRange);
			i++;
		}

	}

	public boolean isUseTimeRange() {
		return useTimeRange;
	}

	public void setUseTimeRange(boolean useTimeRange) {
		this.useTimeRange = useTimeRange;
	}

	public boolean isClassified() {
		return classified;
	}

	public void setClassified(boolean classified) {
		this.classified = classified;
	}

	public int getUrlCount() {
		synchronized (this) {
			return urlCount;
		}
	}

	public int getHashtagCount() {
		synchronized (this) {
			return hashtagCount;
		}

	}

	public int getAt_userCount() {
		synchronized (this) {
			return at_userCount;
		}

	}
}
