package de.unistuttgart.treequest.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.DoubleValuesSource;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;

public class Loader {
//
	private static String stopwordPath = System.getProperty("user.dir") + "\\stopwords.txt";
	public static String indexPath = System.getProperty("user.dir") + "\\twitter_corpus\\index";
//	 private static String stopwordPath = "D:\\Master_Thesis\\MasterAnnotator\\Zheren-TreeQuest\\stopwords.txt";
//	 public static String indexPath = "D:\\Master_Thesis\\MasterAnnotator\\Zheren-TreeQuest\\twitter_corpus\\index";

	public static SpatialContext ctx = SpatialContext.GEO;
	public static SpatialPrefixTree spatialPrefixTree = new GeohashPrefixTree(ctx, 11);
	public static SpatialStrategy strategy = new RecursivePrefixTreeStrategy(spatialPrefixTree, "location");;

	public static Analyzer analyzer;
	public static Directory directory;
	public static IndexSearcher indexSearcher;
	public static IndexReader indexReader;
//	private static Lemmatizer lemmatizer;

	static {
		try {
			//lemmatizer = new Lemmatizer();
			analyzer = new StandardAnalyzer(new BufferedReader(new FileReader(stopwordPath)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		openCSV("C:\\Users\\louzn\\TweetCorpus\\numeric_20110824.csv");
//	}

	public static void openCSV(String filePath) {
		try {
			directory = FSDirectory.open(Paths.get("C:\\Users\\louzn\\TreeQuest\\twitter_corpus\\index"));
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter indexWriter = new IndexWriter(directory, config);

			if (new File(filePath).exists()) {

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(filePath), "UTF8"));
				String line;
				int i = 0, j = 0;
				while ((line = reader.readLine()) != null) {
					String[] t = line.split("\t");
					if (t.length != 7 || line.contains("RT")) {
						i++;
					} else {

						j++;
						Double lat = Double.parseDouble(t[3]);
						Double lon = Double.parseDouble(t[4]);
						if (19.50139D <= lat && lat <= 64.85694D && -161.75583D <= lon && lon <= -68.01197D) {
							indexWriter.addDocument(newSampleDocument(ctx, strategy, t));
						}

					}
				}
				
				reader.close();
				System.out.println("finish");
			}
			indexWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void addTweetMessage(Document doc, String s) throws InterruptedException {
		// @
		String t = s;
		String reg = "\\@\\w+\\b";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(t);
		String atString = "";
		while (m.find()) {
			s = s.replaceAll(m.group(), " ");
			atString = atString + m.group() + " ";
		}
		doc.add(new TextField("@", atString, Field.Store.YES));
		// #

		String reg2 = "\\#\\w+\\b";
		Pattern p2 = Pattern.compile(reg2);
		Matcher m2 = p2.matcher(t);
		String jinString = "";
		while (m2.find()) {
			// s = s.replaceAll(m2.group(), " ");
			jinString = jinString + m2.group() + " ";
		}
		doc.add(new TextField("#", jinString, Field.Store.YES));
		// http
		String reg3 = "(http://|https://)(\\w+(\\.|/))+\\w*\\b";
		Pattern p3 = Pattern.compile(reg3);
		Matcher m3 = p3.matcher(t);
		String urlString = "";
		while (m3.find()) {
			s = s.replaceAll(m3.group(), " ");
			urlString = urlString + m3.group() + " ";
		}
		doc.add(new TextField("url", urlString, Field.Store.YES));

		String reg4 = "([a-zA-Z])(\\1){2,}";
		Pattern p4 = Pattern.compile(reg4, Pattern.CASE_INSENSITIVE);
		Matcher m4 = p4.matcher(t);
		while (m4.find()) {
			s = s.replaceAll(m4.group(), m4.group().substring(0, 1));
		}
		
		//s = lemmatizer.lemmatize(s).toString();
		String reg5 = "\\-\\w+\\-";
		Pattern p5 = Pattern.compile(reg5, Pattern.CASE_INSENSITIVE);
		Matcher m5 = p5.matcher(s);
		while (m5.find()) {
			System.err.println(m5.group());
			s = s.replaceAll(m5.group(), " ");
		}
		s = s.replaceAll("[^0-9a-zA-Z ,.]"," ");

		FieldType ft = new FieldType();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorOffsets(true);
		ft.setStoreTermVectorPayloads(true);
		ft.setStoreTermVectorPositions(true);
		ft.setTokenized(true);
		ft.setStored(true);
		doc.add(new StringField("origin_text", t, Field.Store.YES));
		doc.add(new Field("text", s, ft));

	}

	public static Document newSampleDocument(SpatialContext ctx, SpatialStrategy strategy, String[] s)
			throws InterruptedException {
		Document doc = new Document();
		Long doc_id = Long.parseLong(s[0]);
		Long user_id = Long.parseLong(s[1]);
		Long created_at = Long.parseLong(s[2]);
		Double lat = Double.parseDouble(s[3]);
		Double lon = Double.parseDouble(s[4]);
		String place = s[5];
		String text = s[6];

		// 把数据存储到数据库
		doc.add(new StoredField("doc_id", doc_id));
		doc.add(new LongPoint("doc_id", doc_id));
		doc.add(new StoredField("user_id", user_id));
		doc.add(new NumericDocValuesField("user_id", user_id));
		doc.add(new StoredField("lon", lon));
		doc.add(new DoublePoint("lon", lon));
		doc.add(new StoredField("lat", lat));
		doc.add(new DoublePoint("lat", lat));
		doc.add(new TextField("place", place, Field.Store.YES));
		doc.add(new StoredField("created_at", created_at));
		doc.add(new LongPoint("created_at", created_at));

		Point shape = null;
		Field[] fields = strategy.createIndexableFields((shape = ctx.getShapeFactory().pointXY(lon, lat)));

		for (Field field : fields) {
			doc.add(field);
		}

		doc.add(new StoredField(strategy.getFieldName(), shape.getX() + "," + shape.getY()));
		addTweetMessage(doc, text);

		return doc;

	}

}
