package job_selection;

import java.util.*;

import job_input.JobCollector;

import job_selection.de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import job_selection.de.daslaboratorium.machinelearning.classifier.Classifier;

public class CancellationPredictor {
	static HashMap<Integer, Boolean> jobList;
	static List<Boolean> cancellationList;

	public CancellationPredictor(JobCollector jobCollector) {
		jobList = (HashMap<Integer, Boolean>) jobCollector.getCancellations();
	}

	public static void main(String[] args) {
		JobCollector jobCollector = new JobCollector("/Users/AppleUser/Documents/uni/comp_sci/robot_programming/my_autonomous-warehouse/pc/");
		jobList = jobCollector.getCancellations();
		cancellationList = new ArrayList<>();

		int totalNumTraining = 0;
		int totalNumTest = 0;
		int trueNum = 0;
		int falseNum = 0;
		int correctNum = 0;
		int numOfFeatures = 10;
		Boolean cancellation = false;
		Boolean classification = false;
		Boolean ca = false;
		Boolean cl = false;
		Boolean[] features = null;
		
		//make cancellationList
		for (Map.Entry<Integer, Boolean> entry: jobList.entrySet()) {
		    cancellationList.add(entry.getValue());
		}

		/*
		 * Create a new classifier instance. The context features are Strings and the
		 * context will be classified with a String according to the featureset of the
		 * context.
		 */
		int a = 0;
		final Classifier<Boolean, Boolean> bayes = new BayesClassifier<Boolean, Boolean>();
		System.out.println("\nEXAMPLES:\n");
		for(int i = 0; i < cancellationList.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new Boolean[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = cancellationList.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			if(cancellation) {
				a++;
				bayes.learn(cancellation, Arrays.asList(features));
				trueNum++;
				totalNumTraining++;	
			}
			else {
				if(falseNum < 4500) {
					a++;
					bayes.learn(cancellation, Arrays.asList(features));
					falseNum++;
					totalNumTraining++;
				}
			}
			System.out.println(a + " - cancellation: " + cancellation + ", featureSet: " + Arrays.asList(features).toString());
		}


		/*
		 * The classifier can learn from classifications that are handed over to the
		 * learn methods. Imagine a tokenized text as follows. The tokens are the text's
		 * features. The category of the text will either be positive or negative.
		 */
		//

		/*
		 * Now that the classifier has "learned" two classifications, it will be able to
		 * classify similar sentences. The classify method returns a Classification
		 * Object, that contains the given feature set, classification probability and
		 * resulting category.
		 */

		//classify on test da
		for(int i = 0; i < cancellationList.size() - (numOfFeatures + 1); i++) {
			int counter = 0;
			int maxIndex = i + numOfFeatures;
			features = new Boolean[numOfFeatures];
			for(int j = i; j < maxIndex; j++) {
			    features[counter] = cancellationList.get(j);
			    counter++;  
			}
			cancellation = cancellationList.get(maxIndex);
			classification = bayes.classify(Arrays.asList(features)).getCategory();
			if(cancellation.equals(classification)) {
				correctNum++;
			}
			totalNumTest++;
		}
		Float percent = ((float)correctNum / (float)totalNumTest) * 100;
		
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(features)));
		System.out.println("\n");
		
		System.out.println("\nTRAINING");
		System.out.println("Num of true: " + trueNum);
		System.out.println("Num of false: " + falseNum);
		System.out.println("Total number: " + totalNumTraining);
		
		System.out.println("\nTEST");
		System.out.println("Correct number: " + correctNum);
		System.out.println("Total number: " + totalNumTest);
		System.out.print("Percentage correct: ");
		System.out.printf("%.2f", percent);
		System.out.println("%\n");

//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(unknown1)));
//		
//		System.out.println("Should be true: " + bayes.classify(Arrays.asList(unknown2)).getCategory()); //should output true
//		System.out.println(((BayesClassifier<Boolean, Boolean>) bayes).classifyDetailed(Arrays.asList(unknown2)));

		/*
		 * The BayesClassifier extends the abstract Classifier and provides detailed
		 * classification results that can be retrieved by calling the classifyDetailed
		 * Method.
		 *
		 * The classification with the highest probability is the resulting
		 * classification. The returned List will look like this. [ Classification [
		 * category=negative, probability=0.0078125, featureset=[today, is, a, sunny,
		 * day] ], Classification [ category=positive, probability=0.0234375,
		 * featureset=[today, is, a, sunny, day] ] ]
		 */

		/*
		 * Please note, that this particular classifier implementation will "forget"
		 * learned classifications after a few learning sessions. The number of learning
		 * sessions it will record can be set as follows:
		 */
		bayes.setMemoryCapacity(100000); // remember the last 500 learned classifications
	}

}
