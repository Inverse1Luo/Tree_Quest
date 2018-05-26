package de.unistuttgart.vis.tweet;

import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import de.bwaldvogel.liblinear.Feature;

import org.apache.lucene.document.Document;

public class Tweet {

	private final long tweetID;
	// volatile: Write variable value through to main memory
	private volatile Long dateStamp;
	private volatile Double latitude = null;
	private volatile Double longitude = null;
	private volatile String text = "";
	private volatile String placename = null;
	private volatile String hashtag = null;
	private volatile String at_user = null;

	private volatile String url = null;
	private volatile long userID = 0;
	private volatile Feature[] features;
	private volatile Map<String, Double> termWeigths;
	private volatile Map<String, Long> termCount;

	// Constructors
	public Tweet(long tweetID, long userID) {
		super();
		this.tweetID = tweetID;
		this.userID = userID;
	}

	public Tweet(long tweetID, String userScreenname) {
		super();
		this.tweetID = tweetID;
	}

	public Tweet(Document doc) {
		super();
		this.termWeigths = new HashMap<String, Double>();
		this.termCount = new HashMap<String, Long>();
		this.tweetID = (Long) doc.getField("doc_id").numericValue();
		this.userID = (Long) doc.getField("user_id").numericValue();
		this.dateStamp = (Long) doc.getField("created_at").numericValue();
		this.setText(doc.getField("origin_text").stringValue());
		this.placename = doc.getField("place").stringValue();
		this.latitude = (Double) doc.getField("lat").numericValue();
		this.longitude = (Double) doc.getField("lon").numericValue();
		this.hashtag = doc.getField("#").stringValue();
		this.at_user = doc.getField("@").stringValue();
		this.url = doc.getField("url").stringValue();
	}

	// Setters and Getters
	public Double getLatitude() {
		synchronized (this) {
			return latitude;
		}
	}

	public String getText() {
		synchronized (this) {
			return this.text;
		}
	}

	public long getTweetID() {
		synchronized (this) {
			return tweetID;
		}
	}

	public long getUserID() {
		synchronized (this) {
			return userID;
		}
	}

	public void setText(String text) {
		synchronized (this) {
			this.text = text;
		}
	}

	public void setLatitude(Double latitude) {
		synchronized (this) {
			this.latitude = latitude;
		}
	}

	public Double getLongitude() {
		synchronized (this) {
			return longitude;
		}
	}

	public void setLongitude(Double longitude) {
		synchronized (this) {
			this.longitude = longitude;
		}
	}

	public String getPlacename() {
		synchronized (this) {
			return placename;
		}
	}

	public void setPlacename(String placename) {
		synchronized (this) {
			this.placename = placename;
		}
	}

	public String getHashtag() {
		synchronized (this) {
			return hashtag;
		}
	}

	public void setHashtag(String hashtag) {
		synchronized (this) {
			this.hashtag = hashtag;
		}

	}

	public String getAt_user() {
		synchronized (this) {
			return at_user;
		}
	}

	public void setAt_user(String at_user) {
		synchronized (this) {
			this.at_user = at_user;

		}

	}

	public String getUrl() {
		synchronized (this) {
			return url;
		}

	}

	public void setUrl(String url) {
		synchronized (this) {
			this.url = url;
		}
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public Long getDateStamp() {
		synchronized (this) {
			return dateStamp;
		}
	}

	public Feature[] getFeatures() {
		synchronized (this) {
			return features;
		}
	}

	public void setFeatures(Feature[] features) {
		synchronized (this) {
			this.features = features;
		}
	}

	public Map<String, Double> getTermWeigths() {
		synchronized (this) {
			return termWeigths;
		}
	}

	public void setTermWeigths(Map<String, Double> termWeigths) {
		synchronized (this) {
			this.termWeigths = termWeigths;
		}
	}

	public Map<String, Long> getTermCount() {
		synchronized (this) {
			return termCount;
		}
	}

	public void setTermCount(Map<String, Long> termCount) {
		synchronized (this) {
			this.termCount = termCount;
		}
	}
}
