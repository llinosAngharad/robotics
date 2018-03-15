package job_input;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestJobInput {
	private static final String FILE_LOCATIONS = "files";
	private static final int MAX_X = 9;
	private static final int MAX_Y = 9;
	private static final int MAX_JOB_ID = 99999;
			
	@Test
	void testInstantiation() {
		new JobCollector(FILE_LOCATIONS);
	}

	@Test
	void testGetDropOffs() {
		JobCollector jc = new JobCollector(FILE_LOCATIONS);
		LinkedList<Location> dropOffPoints = (LinkedList<Location>) jc.getDropOffs();
		Assertions.assertTrue(dropOffPoints.size() > 0);
		for (Location l : dropOffPoints) {
			Assertions.assertTrue(0 < l.getX());
			Assertions.assertTrue(MAX_X >= l.getX());
			Assertions.assertTrue(0 < l.getY());
			Assertions.assertTrue(MAX_Y >= l.getY());
		}
	}
	
	@Test
	void testGetCancellations() {
		JobCollector jc = new JobCollector(FILE_LOCATIONS);
		HashMap<Integer,Boolean> cancellations = jc.getCancellations();
		Assertions.assertTrue(cancellations.size() > 0);
		for (Map.Entry<Integer, Boolean> entry: cancellations.entrySet()) {
			Assertions.assertTrue(entry.getKey() >= 0);
			Assertions.assertTrue(entry.getKey() <= MAX_JOB_ID);
			Assertions.assertNotNull(entry.getValue());
		}
	}
	
	@Test
	void testGetJobs() {
		JobCollector jc = new JobCollector(FILE_LOCATIONS);
		LinkedList<Job> jobs = (LinkedList<Job>) jc.getJobs();
		Assertions.assertTrue(jobs.size() > 0);
		for (Job j : jobs) {
			Assertions.assertTrue(j.getID() >= 0);
			Assertions.assertTrue(j.getID() <= MAX_JOB_ID);
			HashMap<Item,Integer> items = j.getItems();
			for (Map.Entry<Item, Integer> entry: items.entrySet()) {
				Assertions.assertTrue(entry.getValue() > 0);
				Item item = entry.getKey();
				Location itemLocation = item.getLocation();
				Assertions.assertTrue(itemLocation.getX() > 0);
				Assertions.assertTrue(itemLocation.getX() <= MAX_X);
				Assertions.assertTrue(itemLocation.getY() > 0);
				Assertions.assertTrue(itemLocation.getY() <= MAX_Y);
				Assertions.assertTrue(item.getReward() > 0.0);
				Assertions.assertTrue(item.getWeight() > 0.0);
			}
		}
	}
	
}
