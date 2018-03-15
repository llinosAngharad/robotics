package job_selection;

import java.util.Comparator;

import job_input.Job;

public class JobComparator implements Comparator<Job> {
    @Override
    public int compare(Job job1, Job job2) {
        return job1.getJobScore() > job2.getJobScore() ? -1 : job1.getJobScore() == job2.getJobScore() ? 0 : 1;
    }
}
