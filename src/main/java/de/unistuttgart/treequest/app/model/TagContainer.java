package de.unistuttgart.treequest.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TagContainer {

	private int TOPTAGS_SIZE = 50;
	private int tweetCount = 0;
	private HashMap<String, Double> termWeights;

	private List<String> topTerms;

	// Constructor
	public TagContainer(List<InfmaTreePoint> points) {
		termWeights = new HashMap<>();
		this.generateTermWeights(points);
		this.tweetCount = points.size();
	}

	// Setters and Getters
	public Map<String, Double> getTermWeights() {
		synchronized (this) {
			return termWeights;
		}
	}

	public int getTweetCount() {
		synchronized (this) {
			return tweetCount;
		}
	}

	// public void generateTermWeights(List<InfmaTreePoint> points) {
	// synchronized (this) {
	// for (InfmaTreePoint point : points) {
	// for (Map.Entry<String, Double> entry :
	// point.getTweet().getTermWeigths().entrySet()) {
	// String tag = entry.getKey();
	// Double weight = entry.getValue();
	// if (termWeights.get(tag) == null) {
	// termWeights.put(tag, weight);
	// } else {
	// if (termWeights.get(tag) < weight) {
	// termWeights.remove(tag);
	// termWeights.put(tag, weight);
	// }
	// }
	// }
	// }
	// }
	// }
	public void generateTermWeights(List<InfmaTreePoint> points) {
		synchronized (this) {
			for (InfmaTreePoint point : points) {
				for (Map.Entry<String, Long> entry : point.getTweet().getTermCount().entrySet()) {
					String tag = entry.getKey();
					Double weight = (double) (entry.getValue());
					if (termWeights.containsKey(tag)) {
						weight = termWeights.get(tag) + weight;
						termWeights.put(tag, weight);
					} else {
						termWeights.put(tag, weight);
					}
				}
			}
		}
	}

	public List<String> getTopTerms() {
		synchronized (this) {
			if (topTerms == null) {
				topTerms = generateTopTags(termWeights);
			}
			return topTerms;
		}
	}

	public List<String> generateTopTags(final HashMap<String, Double> weights) {
		List<String> toptags = sortedKeySet(weights);
		return toptags.subList(0, Math.min(toptags.size(), TOPTAGS_SIZE));
	}

	public static List<String> sortedKeySet(final Map<String, Double> weights) {
		LinkedList<String> toptags = new LinkedList<String>();
		toptags.addAll(weights.keySet());

		Collections.sort(toptags, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				double w1 = weights.get(o1);
				double w2 = weights.get(o2);
				if (w1 > w2) {
					return -1;
				} else if (w1 < w2) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return toptags;
	}

	public void reset() {
		synchronized (this) {
			termWeights.clear();
			topTerms = null;
			tweetCount = 0;
		}
	}

}
