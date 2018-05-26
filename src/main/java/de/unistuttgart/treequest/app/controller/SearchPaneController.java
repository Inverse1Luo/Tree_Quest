package de.unistuttgart.treequest.app.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import org.apache.lucene.search.BooleanQuery;

import de.unistuttgart.treequest.app.model.InfmaTree;
import de.unistuttgart.treequest.app.model.InfmaTreeNode;
import de.unistuttgart.treequest.app.model.QueryManager;
import de.unistuttgart.treequest.app.util.QueryHistory;
import de.unistuttgart.treequest.app.util.mvc.controller.AbstractManagerController;
import de.unistuttgart.treequest.app.util.mvc.controller.AbstractServiceController;
import de.unistuttgart.treequest.app.view.SearchPane;
import de.unistuttgart.vis.tweet.Tweet;

public class SearchPaneController extends AbstractServiceController {

	private SearchPane searchPane;
	private QueryManager queryManager;
	private InfmaTree tweetTree;

	@Override
	protected void setPanel(JPanel jPanel) {
		this.searchPane = (SearchPane) jPanel;
	}

	@Override
	protected void addActionEvents() {
		addActionEvent(searchPane.sendButton, "searchBtn");
	}

	@Override
	protected void showView(boolean isToShow) {
		searchPane.setVisible(isToShow);
	}

	// Constructor
	public SearchPaneController(AbstractManagerController managerController, JPanel jPanel) {
		super(managerController, jPanel);
	}

	// Setters and Getters
	public QueryManager getQueryManager() {
		return queryManager;
	}

	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public InfmaTree getInfmaTree() {
		return tweetTree;
	}

	public void setInfmaTree(InfmaTree infmaTree) {
		this.tweetTree = infmaTree;
	}

	// Action Methods
	public void actionSearchbtn(String command) {
		queryManager.prepareNewQueries();
		// get user inputs from pane
		String keywords = searchPane.keywordField.getText();
		String lat = searchPane.latField.getText();
		String lon = searchPane.lonField.getText();
		String distance = searchPane.distanceField.getText();
		String latRange = searchPane.latRangeField.getText();
		String lonRange = searchPane.lonRangeField.getText();
		String start = searchPane.startField.getText();
		String end = searchPane.endField.getText();
		// remove '-' and ':' from start and end string
		start = Pattern.compile("[^0-9]").matcher(start).replaceAll("").trim();
		end = Pattern.compile("[^0-9]").matcher(end).replaceAll("").trim();
		String url = searchPane.urlField.getText();
		String atUser = searchPane.atUserField.getText();
		String hashtag = searchPane.hashTagField.getText();
		// add queries
		if (!keywords.isEmpty()) {
			queryManager.addTextQuery(keywords);
		}
		if (!lat.isEmpty() && !lon.isEmpty() && !distance.isEmpty()) {
			queryManager.addCircleLocationQuery(lat + " " + lon + " " + distance);
		}
		if (!latRange.isEmpty() && !lonRange.isEmpty()) {
			queryManager.addRectLocationQuery(latRange + " " + lonRange);
		}
		if (!start.isEmpty() && !end.isEmpty() && end.compareTo(start) > 0) {
			queryManager.addTimePeriodQuery(start + " " + end);
		}
		if (!atUser.isEmpty()) {
			queryManager.addAtUserQuery(atUser);
		}
		if (!hashtag.isEmpty()) {
			queryManager.addHashtagQuery(hashtag);
		}
		if (!url.isEmpty()) {
			queryManager.addUrlQuery(url);
		}
		if (tweetTree.getRoot() != null && tweetTree.getSelectedNode() != null
				&& tweetTree.getSelectedNode().getDocids().size() > 0) {
			queryManager.addDocIdQuery(tweetTree.getSelectedNode().getDocids());
		}
		queryManager.setTextReserveRatio(searchPane.textReserveSlider.getValue() / 100.0D);
		// start query
		List<Tweet> response;
		List<Tweet> irResponse;
		BooleanQuery query = queryManager.combineQueries(true);
		BooleanQuery queryIr = queryManager.combineQueries(false);
		QueryHistory queryHistory = new QueryHistory();
		QueryHistory queryHistoryir = new QueryHistory();
		if (tweetTree.getRoot() == null) {
			queryHistory.add(query);
			response = queryManager.searchTweets(query);
			tweetTree.setRoot(new InfmaTreeNode(response, tweetTree, queryHistory, 1D));
		} else {
			if (tweetTree.getSelectedNode() != null && tweetTree.getSelectedNode().getDocids().size() > 0) {
				queryHistory.clear();
				queryHistoryir.clear();
			} else {
				QueryHistory selectedNodeQueryHistory = tweetTree.getSelectedNode().getQueryHistory();
				queryHistory.addAll(selectedNodeQueryHistory);
				queryHistoryir.addAll(selectedNodeQueryHistory);
			}
			queryHistory.add(query);
			queryHistoryir.add(queryIr);
			irResponse = queryManager.searchTweets(queryHistoryir.getHistory());
			response = queryManager.searchTweets(queryHistory.getHistory());
			if (irResponse != null || response != null) {
				InfmaTreeNode left = new InfmaTreeNode(irResponse, tweetTree, queryHistoryir, -1D);
				InfmaTreeNode right = new InfmaTreeNode(response, tweetTree, queryHistory, 1D);
				tweetTree.insert(left, right);
			}
		}
		((TreeQuestController) managerController).displayNewTree(tweetTree);
	}

}
