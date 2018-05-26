package de.unistuttgart.treequest.app.model;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.math.plot.Plot2DPanel;
import org.math.plot.PlotPanel;
import org.math.plot.plots.ScatterPlot;

import com.jujutsu.tsne.barneshut.BHTSne;
import com.jujutsu.tsne.barneshut.BarnesHutTSne;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import de.unistuttgart.vis.tweet.Tweet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;

public class TsneMap {

	Map<String, Integer> dic = new HashMap<String, Integer>();
	Instances instances;

	public double[][] drawTsneMap(List<Tweet> tweets) {
		
		try {
			
			dic = generateDictionary(tweets);
			instances = generateInstance(tweets);
			instances = doPCA(instances);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return doTSNE(instances);
	}

	public Map<String, Integer> generateDictionary(List<Tweet> tweets) {
		Map<String, Integer> dic = new HashMap<String, Integer>();
		int j = 0;
		for (Tweet tweet : tweets) {
			for (Map.Entry<String, Double> termWeight : tweet.getTermWeigths().entrySet()) {
				if (!dic.containsKey(termWeight.getKey()) && termWeight.getKey().matches("\\w+")) {
					dic.put(termWeight.getKey(), j++);
				}
			}
		}
		return dic;
	}

	public Instances generateInstance(List<Tweet> tweets) {
		ArrayList<Attribute> attributes = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : dic.entrySet()) {
			attributes.add(new Attribute(entry.getKey()));
		}
		instances = new Instances("tsne", attributes, 0);
		instances.setClassIndex(instances.numAttributes() - 1);
		for (Tweet tweet : tweets) {
			Instance instance = new DenseInstance(attributes.size());
			for (Map.Entry<String, Double> termWeight : tweet.getTermWeigths().entrySet()) {
				if (dic.get(termWeight.getKey()) != null) {
					int index = dic.get(termWeight.getKey());
					instance.setValue(index, termWeight.getValue());
				}
			}
			instances.add(instance);
		}
		return instances;
	}

	public Instances doPCA(Instances instances) throws Exception {
		PrincipalComponents pca = new PrincipalComponents();
		pca.setInputFormat(instances);
		pca.setMaximumAttributes(50);
		return Filter.useFilter(instances, pca);
	}

	public double[][] doTSNE(Instances instances) {
		final int numRow = instances.size();
		int numCol = instances.numAttributes();
		if (instances.classIndex() > 0) {
			numCol -= 1;
		}
		double[][] data = new double[numRow][numCol];
		for (int i = 0; i < instances.size(); i++) {
			int pos = 0;
			for (int j = 0; j < instances.numAttributes(); j++) {
				if (j != instances.classIndex()) {
					data[i][j] = instances.get(i).value(j);
					pos++;
				}
			}
		}
		BarnesHutTSne tsne;
		tsne = new BHTSne();
		int initial_dims = 50;
		double perplexity = 5.0;
		double[][] Y = tsne.tsne(data, 2, initial_dims, perplexity, 100);
		return Y;

	}

}
