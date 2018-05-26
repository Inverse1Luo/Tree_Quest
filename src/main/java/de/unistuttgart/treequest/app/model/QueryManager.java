package de.unistuttgart.treequest.app.model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRefBuilder;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.unistuttgart.treequest.app.Loader;
import de.unistuttgart.treequest.app.util.QueryHistory.SearchQuery;
import de.unistuttgart.vis.tweet.Tweet;
import edu.stanford.nlp.optimization.QNMinimizer.eLineSearch;
import java_cup.internal_error;

public class QueryManager {
	private LinkedList<Query> queries = new LinkedList<Query>();
	private List<String> textQueryWords = new ArrayList<>() ;
	private double textReserveRatio = 1.0D;
	private JProgressBar progress;
	public Map<String, Integer> _map = null;
	private String[] _array = null;
	// _freqs 记录_map对应位置中的term在多少文档中出现过。用于计算IDF
	private int[] _freqs = null;
	public static SpatialContext ctx = SpatialContext.GEO;
	public static SpatialPrefixTree spatialPrefixTree = new GeohashPrefixTree(ctx, 11);
	public static SpatialStrategy strategy = new RecursivePrefixTreeStrategy(spatialPrefixTree, "location");

	// Constructor
	public QueryManager(JProgressBar progress) {
		this.progress = progress;
		// Since this dictionary for tweets is a huge one, we need a new Thread to run
		// this time consuming task.
		ExecutorService queryExecutor = Executors.newSingleThreadExecutor();
		queryExecutor.execute(new Runnable() {
			@Override
			public void run() {
				createDictionary();
			}
		});
	}

	// Setters and Getters
	public double getTextReserveRatio() {
		return textReserveRatio;
	}

	public void setTextReserveRatio(double textReserveRatio) {
		this.textReserveRatio = textReserveRatio;
	}

	public int getFeatureNumber() {
		return _map.size();
	}

	/**
	 * Create a location search and put it into queries list
	 * 
	 * @param queryStr
	 *            contains geographical search info in the form as: "<latitude>
	 *            <longitude> <distance>" Example:
	 *            queryManager.addLocationQuery("48.7758459000 9.1829321000");
	 */
	public void addCircleLocationQuery(String queryStr) {
		// 地理位置查询方法暂时还没check
		SpatialContext ctx = SpatialContext.GEO;
		SpatialPrefixTree spatialPrefixTree = new GeohashPrefixTree(ctx, 11);
		SpatialStrategy strategy = new RecursivePrefixTreeStrategy(spatialPrefixTree, "location");
		String location[] = queryStr.split(" ");
		double lat = Double.parseDouble(location[0]);
		double lon = Double.parseDouble(location[1]);
		double distance = Double.parseDouble(location[2]);
		// 地理位置查询
		SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, ctx.getShapeFactory().circle(lon, lat,
				DistanceUtils.dist2Degrees(distance, DistanceUtils.EARTH_MEAN_RADIUS_KM)));
		queries.add(strategy.makeQuery(args));
	}
	
	public void addRectLocationQuery(String queryStr) {
		// 地理位置查询方法暂时还没check
		String location[] = queryStr.split(" ");
		String latRange[] = location[0].split(",");
		Double minLat = Double.parseDouble(latRange[0]);
		Double maxLat = Double.parseDouble(latRange[1]);
		
		String lonRange[] = location[1].split(",");
		Double minLon = Double.parseDouble(lonRange[0]);
		Double maxLon = Double.parseDouble(lonRange[1]);
		
		SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, ctx.getShapeFactory().rect(minLon, maxLon, minLat, maxLat));
		queries.add(strategy.makeQuery(args));
	}
	

	/**
	 * Create a temporal search and put it into queries list
	 * 
	 * @param queryStr
	 *            contains chronological search info in the form as: "<start> <end>"
	 *            Example: queryManager.addTimePeriodQuery("201101010101
	 *            201201010101");
	 */
	public void addTimePeriodQuery(String queryStr) {
		String time[] = queryStr.split(" ");
		Long start = Long.parseLong(time[0]);
		Long end = Long.parseLong(time[1]);
		queries.add(LongPoint.newRangeQuery("created_at", start, end));
	}

	/**
	 * Create a hashtag search and put it into queries list
	 * 
	 * @param queryStr
	 *            contains hashtag value in the form as: "<hashtagValue>" Example:
	 *            queryManager.addHashtagQuery("Trump");
	 */
	public void addHashtagQuery(String queryStr) {
		//queries.add(new TermQuery(new Term("#", queryStr)));
		QueryParser queryParser = new QueryParser("#", Loader.analyzer);
		try {
			Query query = queryParser.parse(queryStr);
			queries.add(queryParser.parse(queryStr));
			textQueryWords.add(query.toString().replaceAll("#:", " "));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a "@" user search and put it into queries list
	 * 
	 * @param queryStr
	 *            contains username in the form as: "<username>" Example:
	 *            queryManager.addAtUserQuery("HandsomeIan");
	 */
	public void addAtUserQuery(String queryStr) {
		//queries.add(new TermQuery(new Term("@", queryStr)));
		QueryParser queryParser = new QueryParser("@", Loader.analyzer);
		try {
			Query query = queryParser.parse(queryStr);
			queries.add(queryParser.parse(queryStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a "text" search and put it into queries list set this time's text
	 * query word for further filtering to overcome the problem of overfitting.
	 * 
	 * @param queryStr
	 */

	public void addTextQuery(String queryStr) {
		QueryParser queryParser = new QueryParser("text", Loader.analyzer);
		// queries.add(new TermQuery(new Term("text", queryStr)));
		try {
			Query query = queryParser.parse(queryStr);
			queries.add(queryParser.parse(queryStr));
			textQueryWords.add(query.toString().replaceAll("text:", " "));
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	public void addUrlQuery(String queryStr) {
//		queries.add(new TermQuery(new Term("url", queryStr)));
		QueryParser queryParser = new QueryParser("url", Loader.analyzer);
		try {
			Query query = queryParser.parse(queryStr);
			queries.add(queryParser.parse(queryStr));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public void addDocIdQuery(Collection<Long> docids) {
		queries.add(LongPoint.newSetQuery("doc_id", docids));
	}

	/**
	 * Get ready to form a new query list 1. Clear the queries from last search 2.
	 * Reset the text query word
	 */
	public void prepareNewQueries() {
		queries.clear();
		//textQueryWords = null;
	}

	/**
	 * Combine all queries from the queries list into a single BooleanQuery, this is
	 * recommended to be called after we add all possible queries by methods like
	 * addLocationQuery(), addTimePriodQuery(), addHashtagQuery(), etc.
	 * 
	 * @param clause,
	 *            if it's true then all queries will be marked as Occur.MUST;
	 *            otherwise, all queries in the list will marked as Occur.MUST_NOT.
	 * @return
	 */
	public BooleanQuery combineQueries(boolean clause) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		if (clause) {
			for (Query q : queries) {
				builder.add(q, BooleanClause.Occur.MUST);
			}
		} else {
			for (Query q : queries) {
				if (q.toString().contains("doc_id")) {
					builder.add(q, BooleanClause.Occur.MUST);
				} else {
					builder.add(q, BooleanClause.Occur.MUST_NOT);
				}

			}
		}
		return builder.build();
	}

	/**
	 * Build a new BooleanQuery from a list of SearchQuery.
	 * 
	 * @param queryHistory
	 * @return
	 */
	public BooleanQuery buildQuery(ArrayList<SearchQuery> queries) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();

		// get SearchQuery from every time, add each concrete query to builder
		for (SearchQuery q : queries) {
			// get each concrete query
			List<BooleanClause> booleanClauses = q.getQuery().clauses();
			for (BooleanClause booleanClause : booleanClauses) {
				builder.add(booleanClause.getQuery(), booleanClause.getOccur());
			}
		}
		BooleanQuery booleanQuery = builder.build();
		return booleanQuery;
	}

	/**
	 * Search required tweets based on a list of SearchQuery
	 * 
	 * @param queryHistory
	 * @return
	 */
	public List<Tweet> searchTweets(ArrayList<SearchQuery> queries) {
		return searchTweets(buildQuery(queries));
	}

	public List<Tweet> searchTweets(BooleanQuery booleanQuery) {
		List<Tweet> tweets = new LinkedList<Tweet>();
		try {
			IndexSearcher searcher = Loader.indexSearcher;
			// 根据booleanquery的条件查询所有符合条件的Document
			//TopDocs hits = searcher.search(booleanQuery, Integer.MAX_VALUE);
			TopDocs hits = searcher.search(booleanQuery, 100000);
			if (hits.totalHits > 0) {
				// 随机排序找到的doc，配合下面对关键词的tf移除
				ScoreDoc[] docs = shuffleScoreDocs(hits.scoreDocs);
				int docsLength = docs.length;
				for (int i = 0; i < docsLength; i++) {
					Document hit = searcher.doc(docs[i].doc);
					Tweet tweet = new Tweet(hit);
					setProgressBarValue(i * 1.0 / docsLength);
//					setProgressBarString(i + " / " + docsLength);
					setProgressBarString("Fetching Tweets...");
					// 获取terms
					Terms terms = Loader.indexReader.getTermVector(docs[i].doc, "text");
					if(terms == null) {
						continue;
					}
					TermsEnum iterator = terms.iterator();
					BytesRef bytesRef;
					int allTermsCount = 0;
					// 计算该tweet中有效词总数， 用于tf计算
					while ((bytesRef = iterator.next()) != null) {
						allTermsCount += iterator.totalTermFreq();
					}
					// 重置iterator，用于之后遍历
					iterator = terms.iterator();
					// 确定每个文档有多少个terms
					int size = (int) terms.size();
					String[] texts = new String[size];
					double[] tfs = new double[size];
					int j = 0;
					// 根据关键字保留比例对数组的前半部分记录实际的tf
					// 对其余部分另关键字的tf为0，以此防止overfitting
					if (i < Math.round(docsLength * textReserveRatio)) {
						while ((bytesRef = iterator.next()) != null) {
							texts[j] = bytesRef.utf8ToString();
							tfs[j] = iterator.totalTermFreq() * 1.0D / allTermsCount;
							if (textQueryWords != null && !textQueryWords.toString().contains(bytesRef.utf8ToString())) {
								tweet.getTermCount().put(bytesRef.utf8ToString(), iterator.totalTermFreq());
							}
							j++;
						}
					} else {
						while ((bytesRef = iterator.next()) != null) {
							String text = bytesRef.utf8ToString();
							double tf = iterator.totalTermFreq() * 1.0D / allTermsCount;
							if (textQueryWords != null && textQueryWords.toString().contains(text)) {
								tf = 0.0D;
							}else {
								tweet.getTermCount().put(bytesRef.utf8ToString(), iterator.totalTermFreq());
								
							}	
							
							texts[j] = text;
							tfs[j] = tf;
							j++;
						}
					}
					Feature[] features = getFeature(texts, tfs, tweet);
					tweet.setFeatures(features);
					tweets.add(tweet);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tweets;
	}

	public void setProgressBarValue(double value) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (progress != null) {
					progress.setValue((int) Math.round(value * 100));
				}
			}
		});
	}

	public void setProgressBarString(String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (progress != null) {
					progress.setString(text);
				}
			}
		});
	}
	
	public Feature[] getFeature(String[] texts, double[] tfs, Tweet tweet) {
		// 获得文档总数，用于计算idf
		int totalNumOfDocs = Loader.indexReader.numDocs();
		Feature[] nodes = new Feature[texts.length];
		ComTest[] testnodes = new ComTest[texts.length];
		for (int i = 0; i < texts.length; i++) {
			String text = texts[i];
			int index = _map.get(text);
			int df = _freqs[index];
			double idf = Math.log10(totalNumOfDocs * 1.0D / df);
			if(textQueryWords != null && !textQueryWords.toString().contains(text)) {
//				if (df >2 ) {
					tweet.getTermWeigths().put(text, tfs[i] * idf);
//				}else {
//					tweet.getTermWeigths().put(text, 0D);
//				}
			}

			testnodes[i] = new ComTest(index, tfs[i] * idf);
		}
		// sort test nodes by their index
		Arrays.sort(testnodes);
		int i = 0;
		for (ComTest node : testnodes) {
			nodes[i++] = new FeatureNode(node.index + 1, node.tfidf);
		}

		return nodes;
	}

	public class ComTest implements Comparable {
		int index;
		double tfidf;

		public ComTest(int index, double tfidf) {
			this.index = index;
			this.tfidf = tfidf;
		}

		@Override
		public int compareTo(Object o) {
			return this.index - ((ComTest) o).index;
		}
	}

	private void createDictionary() {
		try {
			// 适合添加删除
			LinkedList<String> termsList = new LinkedList<String>();
			// 适合get set.
			ArrayList<Integer> docFreqs = new ArrayList<Integer>();

			Directory directory = FSDirectory.open(Paths.get(Loader.indexPath));
			DirectoryReader reader = DirectoryReader.open(directory);

			Fields fields = MultiFields.getFields(reader);// 动态合并各个field
			Terms terms = fields.terms("text");// 获取text这个field的所有term
			TermsEnum termsEnum = terms.iterator();//
			CharsRefBuilder spare = new CharsRefBuilder();
			BytesRef textB;
			while ((textB = termsEnum.next()) != null) {
				int df = termsEnum.docFreq();
				// if (df < 50) {
				// continue;
				// }
				spare.copyUTF8Bytes(textB);
				String text = spare.toString();
				// if (text.matches("\\w+") == false) {
				// continue;
				// }
				termsList.add(text);
				// 获取df， docFreq不能用于其他情况，会有错误！
				docFreqs.add(df);

			}
			_array = (String[]) termsList.toArray(new String[termsList.size()]);
			_freqs = new int[termsList.size()];
			// 记录每个term对应_freqs和_array的index
			_map = new HashMap<String, Integer>();
			for (int i = 0; i < _array.length; i++) {
				String term = _array[i];
				_map.put(term, new Integer(i));
				_freqs[i] = docFreqs.get(i);
			}
			reader.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Randomly permutes the specified ScoreDoc array using a default source of
	 * randomness.
	 * 
	 * @param docs,
	 *            an array of ScoreDoc waiting to be shuffled
	 * @return
	 */
	private ScoreDoc[] shuffleScoreDocs(ScoreDoc[] docs) {
		Random random = new Random();
		int docLength = docs.length;
		for (int i = 0; i < docLength; i++) {
			int p = random.nextInt(docLength);
			ScoreDoc tmp = docs[i];
			docs[i] = docs[p];
			docs[p] = tmp;
		}
		return docs;
	}
}