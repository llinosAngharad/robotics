package job_selection;

import java.util.*;

import job_input.Job;
import job_input.JobCollector;

public class JobSelector {
	private ArrayList<Job> jobList;
	private int i;
	
	public JobSelector(JobCollector jobCollector) {
		i=0;
		jobList = (ArrayList<Job>) jobCollector.getJobs();
		Collections.sort(jobList, new JobComparator());
	}
	
	public ArrayList<Job> getJobScores() {
	    return jobList;
	}
	
	public Job selectNextJob() {
	    return jobList.get(i++);
	}
}
