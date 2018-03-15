package job_selection;

import java.util.*;

import job_input.Item;
import job_input.Job;
import job_input.JobCollector;

import job_selection.de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import job_selection.de.daslaboratorium.machinelearning.classifier.Classifier;

public class CancellationPredictor {
	static List<Job> jobList;
	static List<Double> weightList;
	static List<Double> rewardList;
	static List<Integer> itemCountList;
	static HashMap<Integer, Boolean> jobCancellations;
	static List<String> cancellationList;

	public static void main(String[] args) {
		JobCollector jobCollector = new JobCollector("/Users/AppleUser/Documents/uni/comp_sci/robot_programming/my_autonomous-warehouse/pc/");
		jobList = jobCollector.getJobs();
		System.out.println("jobList size: " + jobList.size());

		jobCancellations = jobCollector.getCancellations();
		weightList = new ArrayList<>();
		rewardList = new ArrayList<>();
		itemCountList = new ArrayList<>();
		cancellationList = new ArrayList<>();
		
		for(int i = 0; i < jobList.size(); i++) {
			Job job = jobList.get(i);
			
			int itemCount = job.getTotalItemCount();
			itemCountList.add(itemCount);
			
			double weight = job.getTotalWeight();
			weightList.add(weight);
			
			double reward = job.getTotalReward();
			rewardList.add(reward);
		}
		//make cancellationList
		for (Map.Entry<Integer, Boolean> entry: jobCancellations.entrySet()) {
		    cancellationList.add(entry.getValue().toString());
		}
		
		predictbyCancellation();
		predictByItemCount();
		predictByReward();
		predictByWeight();
	}
	
	public static void predictbyCancellation() {
		final Classifier<String, String> bayes = new BayesClassifier<String, String>();
		int totalNumTraining = 0;
		int totalNumTest = 0;
		int trueNumTraining = 0;
		int falseNumTraining = 0;
		int trueNumTest = 0;
		int falseNumTest = 0;
		int correctNum = 0;
		int numOfFeatures = 10;
		String cancellation;
		String classification;
		String[] features = null;
		
		//learn from cancellations
		for(int i = 0; i < cancellationList.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = cancellationList.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			if(cancellation.equals("true")) {
				bayes.learn(cancellation, Arrays.asList(features));
				trueNumTraining++;
				totalNumTraining++;	
			}
			else {
				if(falseNumTraining < 4500) {
					bayes.learn(cancellation, Arrays.asList(features));
					falseNumTraining++;
					totalNumTraining++;
				}
			}
//			System.out.println(a + " - cancellation: " + cancellation + ", featureSet: " + Arrays.asList(features).toString());
		}
		
		//classify on test data by cancellations
		for(int i = 0; i < cancellationList.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = cancellationList.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			classification = bayes.classify(Arrays.asList(features)).getCategory();
			if(cancellation.equals(classification)) {
				correctNum++;
				if(cancellation.equals("true") && classification.equals("true")) {
					trueNumTest++;
				}
				if(cancellation.equals("false") && classification.equals("false")) {
					falseNumTest++;
				}
			}
			totalNumTest++;
		}
		Float percent = ((float)correctNum / (float)totalNumTest) * 100;
		
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(features)));
		System.out.println("\nFEATURE: CANCELLATIONS\n");
		System.out.println("TRAINING: " + "{true: " + trueNumTraining + ", false: " + falseNumTraining + ", total: " + totalNumTraining + "}");
		System.out.println("TEST");
		System.out.println("Num of times 'true' correctly classified: " + trueNumTest);
		System.out.println("Num of times 'false' correctly classified: " + falseNumTest);
		System.out.println("Total correctly classified: " + correctNum);
		System.out.println("Total number: " + totalNumTest);
		System.out.print("Percentage correct: ");
		System.out.printf("%.2f", percent);
		System.out.println("%\n");
		
		bayes.setMemoryCapacity(100000);
	}
	
	public static void predictByItemCount() {
		final Classifier<String, String> bayes = new BayesClassifier<String, String>();
		int totalNumTraining = 0;
		int totalNumTest = 0;
		int trueNumTraining = 0;
		int falseNumTraining = 0;
		int trueNumTest = 0;
		int falseNumTest = 0;
		int correctNum = 0;
		int numOfFeatures = 10;
		String cancellation;
		String classification;
		String[] features = null;
		List<String> itemCountListStrings = new ArrayList<>();
		for(int k = 0; k < itemCountList.size(); k++) {
			itemCountListStrings.add(itemCountList.get(k).toString());
		}
		//learn from cancellations
		for(int i = 0; i < itemCountListStrings.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = itemCountListStrings.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			if(cancellation.equals("true")) {
				bayes.learn(cancellation, Arrays.asList(features));
				trueNumTraining++;
				totalNumTraining++;	
			}
			else {
				if(falseNumTraining < 4500) {
					bayes.learn(cancellation, Arrays.asList(features));
					falseNumTraining++;
					totalNumTraining++;
				}
			}
		}
		
		//classify on test data by cancellations
		for(int i = 0; i < itemCountListStrings.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = itemCountListStrings.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			classification = bayes.classify(Arrays.asList(features)).getCategory();
			if(cancellation.equals(classification)) {
				correctNum++;
				if(cancellation.equals("true") && classification.equals("true")) {
					trueNumTest++;
				}
				if(cancellation.equals("false") && classification.equals("false")) {
					falseNumTest++;
				}
			}
			totalNumTest++;
		}
		Float percent = ((float)correctNum / (float)totalNumTest) * 100;
		
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(features)));
		System.out.println("\nFEATURE: ITEM COUNT\n");
		
		System.out.println("TRAINING: " + "{true: " + trueNumTraining + ", false: " + falseNumTraining + ", total: " + totalNumTraining + "}");
		System.out.println("TEST");
		System.out.println("Num of times 'true' correctly classified: " + trueNumTest);
		System.out.println("Num of times 'false' correctly classified: " + falseNumTest);
		System.out.println("Total correctly classified: " + correctNum);
		System.out.println("Total number: " + totalNumTest);
		System.out.print("Percentage correct: ");
		System.out.printf("%.2f", percent);
		System.out.println("%\n");
		
		bayes.setMemoryCapacity(100000);
	}
	
	public static void predictByReward() {
		final Classifier<String, String> bayes = new BayesClassifier<String, String>();
		int totalNumTraining = 0;
		int totalNumTest = 0;
		int trueNumTraining = 0;
		int falseNumTraining = 0;
		int trueNumTest = 0;
		int falseNumTest = 0;
		int correctNum = 0;
		int numOfFeatures = 10;
		String cancellation;
		String classification;
		String[] features = null;
		List<String> rewardStrings = new ArrayList<>();
		for(int k = 0; k < rewardList.size(); k++) {
			rewardStrings.add(rewardList.get(k).toString());
		}
		//learn from cancellations
		for(int i = 0; i < rewardStrings.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = rewardStrings.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			if(cancellation.equals("true")) {
				bayes.learn(cancellation, Arrays.asList(features));
				trueNumTraining++;
				totalNumTraining++;	
			}
			else {
				if(falseNumTraining < 4500) {
					bayes.learn(cancellation, Arrays.asList(features));
					falseNumTraining++;
					totalNumTraining++;
				}
			}
		}
		
		//classify on test data by cancellations
		for(int i = 0; i < rewardStrings.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = rewardStrings.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			classification = bayes.classify(Arrays.asList(features)).getCategory();
			if(cancellation.equals(classification)) {
				correctNum++;
				if(cancellation.equals("true") && classification.equals("true")) {
					trueNumTest++;
				}
				if(cancellation.equals("false") && classification.equals("false")) {
					falseNumTest++;
				}
			}
			totalNumTest++;
		}
		Float percent = ((float)correctNum / (float)totalNumTest) * 100;
		
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(features)));
		System.out.println("\nFEATURE: REWARD\n");
		
		System.out.println("TRAINING: " + "{true: " + trueNumTraining + ", false: " + falseNumTraining + ", total: " + totalNumTraining + "}");
		System.out.println("TEST");
		System.out.println("Num of times 'true' correctly classified: " + trueNumTest);
		System.out.println("Num of times 'false' correctly classified: " + falseNumTest);
		System.out.println("Total correctly classified: " + correctNum);
		System.out.println("Total number: " + totalNumTest);
		System.out.print("Percentage correct: ");
		System.out.printf("%.2f", percent);
		System.out.println("%\n");
		
		bayes.setMemoryCapacity(100000);
		
	}
	
	public static void predictByWeight() {
		final Classifier<String, String> bayes = new BayesClassifier<String, String>();
		int totalNumTraining = 0;
		int totalNumTest = 0;
		int trueNumTraining = 0;
		int falseNumTraining = 0;
		int trueNumTest = 0;
		int falseNumTest = 0;
		int correctNum = 0;
		int numOfFeatures = 10;
		String cancellation;
		String classification;
		String[] features = null;
		List<String> weightStrings = new ArrayList<>();
		for(int k = 0; k < weightList.size(); k++) {
			weightStrings.add(weightList.get(k).toString());
		}
		//learn from cancellations
		for(int i = 0; i < weightStrings.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = weightStrings.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			if(cancellation.equals("true")) {
				bayes.learn(cancellation, Arrays.asList(features));
				trueNumTraining++;
				totalNumTraining++;	
			}
			else {
				if(falseNumTraining < 4500) {
					bayes.learn(cancellation, Arrays.asList(features));
					falseNumTraining++;
					totalNumTraining++;
				}
			}
		}
		
		//classify on test data by cancellations
		for(int i = 0; i < weightStrings.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new String[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = weightStrings.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			classification = bayes.classify(Arrays.asList(features)).getCategory();
			if(cancellation.equals(classification)) {
				correctNum++;
				if(cancellation.equals("true") && classification.equals("true")) {
					trueNumTest++;
				}
				if(cancellation.equals("false") && classification.equals("false")) {
					falseNumTest++;
				}
			}
			totalNumTest++;
		}
		Float percent = ((float)correctNum / (float)totalNumTest) * 100;
		
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(features)));
		System.out.println("\nFEATURE: WEIGHT\n");
		
		System.out.println("TRAINING: " + "{true: " + trueNumTraining + ", false: " + falseNumTraining + ", total: " + totalNumTraining + "}");
		System.out.println("TEST");
		System.out.println("Num of times 'true' correctly classified: " + trueNumTest);
		System.out.println("Num of times 'false' correctly classified: " + falseNumTest);
		System.out.println("Total correctly classified: " + correctNum);
		System.out.println("Total number: " + totalNumTest);
		System.out.print("Percentage correct: ");
		System.out.printf("%.2f", percent);
		System.out.println("%\n");
		
		bayes.setMemoryCapacity(100000);
	}
}
