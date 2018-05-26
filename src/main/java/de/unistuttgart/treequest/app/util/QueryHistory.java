package de.unistuttgart.treequest.app.util;

import java.util.ArrayList;

import org.apache.lucene.search.BooleanQuery;

public class QueryHistory {

	/**
	 * Each time, search query from user will be transformed into BooleanQuery. This
	 * makes us capable of just using BooleanQuery in history list, however, a
	 * wrapper class, like what we create here, SearchQuery, should be used instead.
	 * It gives us chances to add something else in the future to each search query.
	 */
	public class SearchQuery {
		private final BooleanQuery query;

		public SearchQuery(BooleanQuery q) {
			this.query = q;
		}

		public BooleanQuery getQuery() {
			return query;
		}
	}

	private ArrayList<SearchQuery> history = new ArrayList<SearchQuery>();

	public ArrayList<SearchQuery> getHistory() {
		synchronized (this) {
			return history;
		}
	}

	public void addAll(QueryHistory other) {
		synchronized (this) {
			history.addAll(other.getHistory());
		}
	}

	public void add(BooleanQuery q) {
		synchronized (this) {
			history.add(new SearchQuery(q));
		}
	}

	public void clear() {
		synchronized (this) {
			history.clear();
		}
	}
}
