package de.unistuttgart.treequest.app.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;
import de.unistuttgart.vis.tweet.Tweet;

public class LibLinearClassifier {

	private Model model;
	private Problem p;
	private double C;
	private double[] weights;
	private Parameter param;
	private int nr_fold = 10;
	protected double _bias = 0D;

	public void train(InfmaTreeNode node, InfmaTreeNode irnode, int dimNum) {
		synchronized (node) {
			synchronized (irnode) {
				p = this.createProblem(node, irnode, dimNum);
				//weights = new double[] { 1.0D, 1.0D * irnode.getTweets().size() / node.getTweets().size() };
				weights = new double[] { 1.0D, 1.0D};
			}
		}
		C = Linear.findParameterC(p, new Parameter(SolverType.L2R_L2LOSS_SVC, 1, Double.POSITIVE_INFINITY, 0.1), 5,
				-1.0, 100).getBestC();
		param = new Parameter(SolverType.L2R_L2LOSS_SVC_DUAL, C, 0.001);
		// use node size and irnode size to determine weights, to prevent unbalancing
		// problem
		int[] weightLabels = { -1, 1 };
		param.setWeights(weights, weightLabels);
		doCrossValidation();
		model = Linear.train(p, param);

		node.setClassified(true);
		irnode.setClassified(true);
//		predict(node.getPoints());
//		predict(irnode.getPoints());
	}

	public void predict(List<InfmaTreePoint> points) {
		synchronized (points) {
			for (InfmaTreePoint point : points) {
				double[] dec_values = new double[model.getNrClass()];
				double label = Linear.predictValues(model, point.getTweet().getFeatures(), dec_values);
				point.setRelC(label);
				point.setDecVal(dec_values[0]);
			}
		}
	}

	// public Problem createProblem(InfmaTreeNode node, InfmaTreeNode irnode, int
	// dimNum) {
	// double[] targetValues = new double[node.getTweets().size() +
	// irnode.getTweets().size()];
	// for (int i = 0; i < targetValues.length; i++) {
	// if (i < node.getTweets().size()) {
	// targetValues[i] = 1;
	// } else {
	// targetValues[i] = -1;
	// }
	// }
	//
	// Feature[][] featureMatrix = new Feature[targetValues.length][];
	// int i = 0;
	// for (Tweet tweet : node.getTweets()) {
	// featureMatrix[i] = tweet.getFeatures();
	// i++;
	// }
	// for (Tweet tweet : irnode.getTweets()) {
	// featureMatrix[i] = tweet.getFeatures();
	// i++;
	// }
	//
	// final Problem prob = new Problem();
	// prob.y = targetValues;
	// prob.l = featureMatrix.length;
	// prob.bias = _bias < 0.0D ? 0.0D : _bias;
	// prob.n = dimNum;
	// prob.x = featureMatrix;
	// return prob;
	// }

	public Problem createProblem(InfmaTreeNode node, InfmaTreeNode irnode, int dimNum) {
		int size = node.getTweets().size() < irnode.getTweets().size() ? node.getTweets().size()
				: irnode.getTweets().size();
		double[] targetValues = new double[2*size];
		for (int i = 0; i < 2*size; i++) {
			if (i < size) {
				targetValues[i] = 1;
			} else {
				targetValues[i] = -1;
			}
		}

		Feature[][] featureMatrix = new Feature[targetValues.length][];
		int i = 0;
		List<Tweet> tweets = new LinkedList<Tweet>();
		List<Tweet> irtweets = new LinkedList<Tweet>();
		tweets = node.getTweets();
		irtweets = irnode.getTweets();
	
		if (tweets.size()>size) {
			tweets = getRandomList(tweets, size);	
		}
		
		if (irtweets.size()>size) {
			irtweets = getRandomList(irtweets, size);	
		}
//		for (Tweet tweet : node.getTweets()) {
//			featureMatrix[i] = tweet.getFeatures();
//			i++;
//			if(i >= size ) {
//				break;
//			}
//		}
//		for (Tweet tweet : irnode.getTweets()) {
//			featureMatrix[i] = tweet.getFeatures();
//			i++;
//			if(i >= targetValues.length ) {
//				break;
//			}
//		}
		
		for (Tweet tweet : tweets) {
		featureMatrix[i] = tweet.getFeatures();
		i++;
		if(i >= size ) {
			break;
		}
	}
	for (Tweet tweet : irtweets) {
		featureMatrix[i] = tweet.getFeatures();
		i++;
		if(i >= targetValues.length ) {
			break;
		}
	}

		final Problem prob = new Problem();
		prob.y = targetValues;
		prob.l = featureMatrix.length;
		prob.bias = _bias < 0.0D ? 0.0D : _bias;
		prob.n = dimNum;
		prob.x = featureMatrix;
		return prob;
	}
	
	public List getRandomList(List paramList,int count){
        if(paramList.size()<count){
            return paramList;
        }
        Random random=new Random();
        List<Integer> tempList=new ArrayList<Integer>();
        List<Object> newList=new ArrayList<Object>();
        int temp=0;
        for(int i=0;i<count;i++){
            temp=random.nextInt(paramList.size());//将产生的随机数作为被抽list的索引
            if(!tempList.contains(temp)){
                tempList.add(temp);
                newList.add(paramList.get(temp));
            }
            else{
                i--;
            }   
        }
        return newList;
    }

	private void doCrossValidation() {
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[p.l];
		long start, stop;
		start = System.currentTimeMillis();
		Linear.crossValidation(p, param, nr_fold, target);
		stop = System.currentTimeMillis();
		System.out.println("time: " + (stop - start) + " ms");
		if (param.getSolverType().isSupportVectorRegression()) {
			for (int i = 0; i < p.l; i++) {
				double y = p.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			System.out.printf("Cross Validation Mean squared error = %g%n", total_error / p.l);
			System.out.printf("Cross Validation Squared correlation coefficient = %g%n", //
					((p.l * sumvy - sumv * sumy) * (p.l * sumvy - sumv * sumy))
							/ ((p.l * sumvv - sumv * sumv) * (p.l * sumyy - sumy * sumy)));
		} else {
			int total_correct = 0;
			for (int i = 0; i < p.l; i++)
				if (target[i] == p.y[i])
					++total_correct;
			System.out.printf("correct: %d%n", total_correct);
			System.out.printf("Cross Validation Accuracy = %g%%%n", 100.0 * total_correct / p.l);
		}
	}

	public void saveModel() {
		try {
			File modelFile = new File("Model");
			model.save(modelFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
