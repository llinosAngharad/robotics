package job_selection;

import java.util.*;

import job_input.Item;
import job_input.Job;
import job_input.JobCollector;

import job_selection.de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import job_selection.de.daslaboratorium.machinelearning.classifier.*;

public class CancellationPredictor {
	static List<Job> jobList;
	static List<Double> weightList;
	static List<Double> rewardList;
	static List<Integer> itemCountList;
	static HashMap<Integer, Boolean> jobCancellations;
	static List<Boolean> cancellationList;
	static List<Boolean> finalCancellationList;

	public static void main(String[] args) {
		JobCollector jobCollector = new JobCollector("/Users/AppleUser/Documents/uni/comp_sci/robot_programming/my_autonomous-warehouse/pc");
		jobList = jobCollector.getJobs();
		System.out.println("\njobList size: " + jobList.size());

		jobCancellations = jobCollector.getCancellations();
		weightList = new ArrayList<>();
		rewardList = new ArrayList<>();
		itemCountList = new ArrayList<>();
		cancellationList = new ArrayList<>();
		finalCancellationList = new ArrayList<>();
		
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
		    cancellationList.add(entry.getValue());
		}
		
		System.out.println("itemCountList size: " + itemCountList.size());
		System.out.println("weightList size: " + weightList.size());
		System.out.println("rewardList size: " + rewardList.size());
		System.out.println("cancellationList size: " + cancellationList.size());

		predict();
	}
	
	public static void predict() {
		final Classifier<List<Double>, Boolean> bayes = new BayesClassifier<List<Double>, Boolean>();
		int totalNumTraining = 0;
		int totalNumTest = 0;
		int trueNumTraining = 0;
		int falseNumTraining = 0;
		int trueNumCorrectTest = 0;
		int falseNumCorrectTest = 0;
		int trueNumWrongTest = 0;
		int falseNumWrongTest = 0;
		int correctNum = 0;
		int numOfFeatures = 3;
		List<Double> features;

		//learn from training data
		for(int i = 0; i < jobList.size() - (numOfFeatures + 1); i++) {
			features = new ArrayList<Double>();
			features.add(0, (double) itemCountList.get(i));
			features.add(1, weightList.get(i));
			features.add(2, rewardList.get(i));
			Boolean cancellation = cancellationList.get(i);
			Classification<List<Double>, Boolean> classification = new Classification<List<Double>, Boolean>(Arrays.asList(features), cancellation);
			
			// Unfiltered code - returns 77.45% accuracy
			bayes.learn(classification);
			
			//Filtered code to make data set 50/50 - returns 22.62% accuracy
//			if(cancellation) {
//				bayes.learn(classification);
//				trueNumTraining++;
//				totalNumTraining++;
//			}
//			else {
//				if(falseNumTraining < 12000) {
//					bayes.learn(classification);
//					falseNumTraining++;
//					totalNumTraining++;
//				}
//			}
		}
		
		//classify from test data
		for(int i = 0; i < jobList.size() - (numOfFeatures + 1); i++) {
			features = new ArrayList<Double>();
			features.add(0, (double) itemCountList.get(i));
			features.add(1, weightList.get(i));
			features.add(2, rewardList.get(i));
			Boolean cancellation = cancellationList.get(i);
			Boolean classification = bayes.classify(Arrays.asList(features)).getCategory();
			if(cancellation.equals(classification)) {
				correctNum++;
				if(cancellation && classification) {
					trueNumCorrectTest++;
				}
				if(!cancellation && !classification) {
					falseNumCorrectTest++;
				}
			}
			else {
				if(cancellation) {
					trueNumWrongTest++;
				}
				if(!cancellation) {
					falseNumWrongTest++;
				}
			}
			totalNumTest++;
		}
		Float percent = ((float)correctNum / (float)totalNumTest) * 100;
		
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(features)));
		System.out.println("\nRESULTS\n");
		
		System.out.println("TRAINING: " + "{true: " + trueNumTraining + ", false: " + falseNumTraining + ", total: " + totalNumTraining + "}");
		System.out.println("\nTEST:\n");
		System.out.println("Num of times 'true' correctly classified: " + trueNumCorrectTest);
		System.out.println("Num of times 'false' correctly classified: " + falseNumCorrectTest);
		System.out.println("Num of times 'true' INcorrectly classified: " + trueNumWrongTest);
		System.out.println("Num of times 'false' INcorrectly classified: " + falseNumWrongTest);
		System.out.println("\nTotal correctly classified: " + correctNum);
		System.out.println("Total number: " + totalNumTest);
		System.out.print("Percentage correct: ");
		System.out.printf("%.2f", percent);
		System.out.println("%\n");
		
		bayes.setMemoryCapacity(100000);
	}
}
